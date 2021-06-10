/**
 * Copyright 2019 Pratanu Mandal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package net.vitox.client.util;

import net.vitox.client.util.exception.Unique4jException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * The <code>Unique4j</code> class is the primary logical entry point to the library.<br>
 * It allows to create an application lock or free it and send and receive messages between first and subsequent instances.<br><br>
 * 
 * <pre>
 *	// unique application ID
 *	String APP_ID = "tk.pratanumandal.unique4j-mlsdvo-20191511-#j.6";
 *	
 *	// create Unique4j instance
 *	Unique4j unique = new Unique4j(APP_ID) {
 *	&nbsp;&nbsp;&nbsp;&nbsp;&#64;Override
 *	&nbsp;&nbsp;&nbsp;&nbsp;protected void receiveMessage(String message) {
 *	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// print received message (timestamp)
 *	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println(message);
 *	&nbsp;&nbsp;&nbsp;&nbsp;}
 *	&nbsp;&nbsp;&nbsp;&nbsp;
 *	&nbsp;&nbsp;&nbsp;&nbsp;&#64;Override
 *	&nbsp;&nbsp;&nbsp;&nbsp;protected String sendMessage() {
 *	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// send timestamp as message
 *	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Timestamp ts = new Timestamp(new Date().getTime());
 *	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return "Another instance launch attempted: " + ts.toString();
 *	&nbsp;&nbsp;&nbsp;&nbsp;}
 *	};
 *	
 *	// try to obtain lock
 *	try {
 *	&nbsp;&nbsp;&nbsp;&nbsp;unique.acquireLock();
 *	} catch (Unique4jException e) {
 *	&nbsp;&nbsp;&nbsp;&nbsp;e.printStackTrace();
 *	}
 *	
 *	...
 *	
 *	// try to free the lock before exiting program
 *	try {
 *	&nbsp;&nbsp;&nbsp;&nbsp;unique.freeLock();
 *	} catch (Unique4jException e) {
 *	&nbsp;&nbsp;&nbsp;&nbsp;e.printStackTrace();
 *	}
 * </pre>
 * 
 * @author Pratanu Mandal
 * @since 1.3
 *
 */
public abstract class              Unique4j {
	
	// starting position of port check
	private static final int PORT_START = 3000;
	
	// system temporary directory path
	public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
	
	/**
	 * Unique string representing the application ID.<br><br>
	 * 
	 * The APP_ID must be as unique as possible.
	 * Avoid generic names like "my_app_id" or "hello_world".<br>
	 * A good strategy is to use the entire package name (group ID + artifact ID) along with some random characters.
	 */
	public final String APP_ID;
	
	// auto exit from application or not
	private final boolean AUTO_EXIT;
	
	// lock server port
	private int port;
	
	// lock server socket
	private ServerSocket server;
	
	// lock file RAF object
	private RandomAccessFile lockRAF;
	
	// file lock for the lock file RAF object
	private FileLock fileLock;

	/**
	 * Parameterized constructor.<br>
	 * This constructor configures to automatically exit the application for subsequent instances.<br><br>
	 * 
	 * The APP_ID must be as unique as possible.
	 * Avoid generic names like "my_app_id" or "hello_world".<br>
	 * A good strategy is to use the entire package name (group ID + artifact ID) along with some random characters.
	 * 
	 * @param APP_ID Unique string representing the application ID
	 */
	public Unique4j(final String APP_ID) {
		this(APP_ID, true);
	}
	
	/**
	 * Parameterized constructor.<br>
	 * This constructor allows to explicitly specify the exit strategy for subsequent instances.<br><br>
	 * 
	 * The APP_ID must be as unique as possible.
	 * Avoid generic names like "my_app_id" or "hello_world".<br>
	 * A good strategy is to use the entire package name (group ID + artifact ID) along with some random characters.
	 * 
	 * @since 1.2
	 * 
	 * @param APP_ID Unique string representing the application ID
	 * @param AUTO_EXIT If true, automatically exit the application for subsequent instances
	 */
	public Unique4j(final String APP_ID, final boolean AUTO_EXIT) {
		this.APP_ID = APP_ID;
		this.AUTO_EXIT = AUTO_EXIT;
	}
	
	/**
	 * Try to obtain lock. If not possible, send data to first instance.
	 * 
	 * @deprecated Use <code>acquireLock()</code> instead.
	 * @throws Unique4jException throws Unique4jException if it is unable to start a server or connect to server
	 */
	@Deprecated
	public void lock() throws Unique4jException {
		acquireLock();
	}
	
	/**
	 * Try to obtain lock. If not possible, send data to first instance.
	 * 
	 * @since 1.2
	 * 
	 * @return true if able to acquire lock, false otherwise
	 * @throws Unique4jException throws Unique4jException if it is unable to start a server or connect to server
	 */
	public boolean acquireLock() throws Unique4jException {
		// try to obtain port number from lock file
		port = lockFile();
		
		if (port == -1) {
			// failed to fetch port number
			// try to start server
			startServer();
		}
		else {
			// port number fetched from lock file
			// try to start client
			return doClient();
		}
		
		return (server != null);
	}
	
	// start the server
	private void startServer() throws Unique4jException {
		// try to create server
		port = PORT_START;
		while (true) {
			try {
				server = new ServerSocket(port);
				break;
			} catch (IOException e) {
				port++;
			}
		}
		
		// try to lock file
		lockFile(port);
		
		// server created successfully; this is the first instance
		// keep listening for data from other instances
		Thread thread = new Thread() {
			@Override
			public void run() {
				while (!server.isClosed()) {
					try {
						// establish connection
						final Socket socket = server.accept();
						
						// handle socket on a different thread to allow parallel connections
						Thread thread = new Thread() {
							@Override
							public void run() {
								try {
									// open writer
									OutputStream os = socket.getOutputStream();
									DataOutputStream dos = new DataOutputStream(os);
									
									// open reader
									InputStream is = socket.getInputStream();
									DataInputStream dis = new DataInputStream(is);
									
									// read message length from client
									int length = dis.readInt();
									
									// read message string from client
									String message = null;
									if (length > -1) {
										byte[] messageBytes = new byte[length];
										int bytesRead = dis.read(messageBytes, 0, length);
										message = new String(messageBytes, 0, bytesRead, "UTF-8");
									}
									
									// write response to client
									if (APP_ID == null) {
										dos.writeInt(-1);
									}
									else {
										byte[] appId = APP_ID.getBytes("UTF-8");
										
										dos.writeInt(appId.length);
										dos.write(appId);
									}
									dos.flush();
									
									// close writer and reader
									dos.close();
									dis.close();
									
									// perform user action on message
									receiveMessage(message);
									
									// close socket
									socket.close();
								} catch (IOException e) {
									handleException(new Unique4jException(e));
								}
							}
						};
						
						// start socket thread
						thread.start();
					} catch (SocketException e) {
						if (!server.isClosed()) {
							handleException(new Unique4jException(e));
						}
					} catch (IOException e) {
						handleException(new Unique4jException(e));
					}
				}
			}
		};
		
		thread.start();
	}
	
	private boolean doClient() throws Unique4jException {
		InetAddress address = null;
		try {
			address = InetAddress.getByName(null);
		} catch (UnknownHostException e) {
			throw new Unique4jException(e);
		}
		
		Socket socket = null;
		try {
			socket = new Socket(address, port);
		} catch (IOException e) {
			startServer();
		}
		
		if (socket != null) {
			try {
				String message = sendMessage();
				
				OutputStream os = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				
				InputStream is = socket.getInputStream();
				DataInputStream dis = new DataInputStream(is);
				
				if (message == null) {
					dos.writeInt(-1);
				}
				else {
					byte[] messageBytes = message.getBytes("UTF-8");
					
					dos.writeInt(messageBytes.length);
					dos.write(messageBytes);
				}
				
				dos.flush();
				
				int length = dis.readInt();
				
				String response = null;
				if (length > -1) {
					byte[] responseBytes = new byte[length];
					int bytesRead = dis.read(responseBytes, 0, length);
					response = new String(responseBytes, 0, bytesRead, "UTF-8");
				}
				
				dos.close();
				dis.close();
				
				if (response.equals(APP_ID)) {
					if (AUTO_EXIT) {
						beforeExit();
						return false;
					}
				}
				else {
					startServer();
				}
			} catch (IOException e) {
				throw new Unique4jException(e);
			} finally {
				// close socket
			 	try {
			 		if (socket != null) socket.close();
				} catch (IOException e) {
					throw new Unique4jException(e);
				}
			}
		}

		return true;
	}
	
	// try to get port from lock file
	private int lockFile() throws Unique4jException {
		// lock file path
		String filePath = TEMP_DIR + File.separator + APP_ID + ".lock";
		File file = new File(filePath);
		
		// try to get port from lock file
		if (file.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				return Integer.parseInt(br.readLine());
			} catch (IOException e) {
				throw new Unique4jException(e);
			} catch (NumberFormatException e) {
				// do nothing
			} finally {
				try {
					if (br != null) br.close();
				} catch (IOException e) {
					throw new Unique4jException(e);
				}
			}
		}
		
		return -1;
	}
	
	// try to write port to lock file
	private void lockFile(int port) throws Unique4jException {
		// lock file path
		String filePath = TEMP_DIR + File.separator + APP_ID + ".lock";
		File file = new File(filePath);
		
		// try to write port to lock file
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			bw.write(String.valueOf(port));
		} catch (IOException e) {
			throw new Unique4jException(e);
		} finally {
			try {
				if (bw != null) bw.close();
			} catch (IOException e) {
				throw new Unique4jException(e);
			}
		}
		
		// try to obtain file lock
		try {
			lockRAF = new RandomAccessFile(file, "rw");
			FileChannel fc = lockRAF.getChannel();
			fileLock = fc.tryLock(0, (~0) >>> 1, true);
			if (fileLock == null) {
				throw new Unique4jException("Failed to obtain file lock");
			}
		} catch (FileNotFoundException e) {
			throw new Unique4jException(e);
		} catch (IOException e) {
			throw new Unique4jException(e);
		}
	}
	
	/**
	 * Free the lock if possible. This is only required to be called from the first instance.
	 * 
	 * @deprecated Use <code>freeLock()</code> instead.
	 * @throws Unique4jException throws Unique4jException if it is unable to stop the server or release file lock
	 */
	@Deprecated
	public void free() throws Unique4jException {
		freeLock();
	}
	
	/**
	 * Free the lock if possible. This is only required to be called from the first instance.
	 * 
	 * @since 1.2
	 * 
	 * @return true if able to release lock, false otherwise
	 * @throws Unique4jException throws Unique4jException if it is unable to stop the server or release file lock
	 */
	public boolean freeLock() throws Unique4jException {
		try {
			// close server socket
			if (server != null) {
				server.close();
			
				// lock file path
				String filePath = TEMP_DIR + File.separator + APP_ID + ".lock";
				File file = new File(filePath);
				
				// try to release file lock
				if (fileLock != null) {
					fileLock.release();
				}
				
				// try to close lock file RAF object
				if (lockRAF != null) {
					lockRAF.close();
				}
				
				// try to delete lock file
				if (file.exists()) {
					file.delete();
				}
				
				return true;
			}
			
			return false;
		} catch (IOException e) {
			throw new Unique4jException(e);
		}
	}
	
	/**
	 * Method used in first instance to receive messages from subsequent instances.<br><br>
	 * 
	 * This method is not synchronized.
	 * 
	 * @param message message received by first instance from subsequent instances
	 */
	protected abstract void receiveMessage(String message);
	
	/**
	 * Method used in subsequent instances to send message to first instance.<br><br>
	 * 
	 * It is not recommended to perform blocking (long running) tasks here. Use <code>beforeExit()</code> method instead.<br>
	 * One exception to this rule is if you intend to perform some user interaction before sending the message.<br><br>
	 * 
	 * This method is not synchronized.
	 * 
	 * @return message sent from subsequent instances
	 */
	protected abstract String sendMessage();
	
	/**
	 * Method to receive and handle exceptions occurring while first instance is listening for subsequent instances.<br><br>
	 * 
	 * By default prints stack trace of all exceptions. Override this method to handle exceptions explicitly.<br><br>
	 * 
	 * This method is not synchronized.
	 * 
	 * @param exception exception occurring while first instance is listening for subsequent instances
	 */
	protected void handleException(Exception exception) {
		exception.printStackTrace();
	}
	
	/**
	 * This method is called before exiting from subsequent instances.<br><br>
	 * 
	 * Override this method to perform blocking tasks before exiting from subsequent instances.<br>
	 * This method is not invoked if auto exit is turned off.<br><br>
	 * 
	 * This method is not synchronized.
	 * 
	 * @since 1.2
	 */
	protected void beforeExit() {}
	
}
