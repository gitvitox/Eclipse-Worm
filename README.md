# Eclipse-Worm
Eclipse is a self spreading worm developed in Java. You can infect a single Minecraft plugin, which will then iterate through all plugins and also infect them.
For educational purposes only!

# How to use it
This is a console based application. Type .help after you started the program and most things will be clear.

# How it works
After the program is started, a TCP Server will get setup. You can then infect a Minecraft Plugin .jar file. After the infected plugin is started on a Minecraft Server, it will then iterate through each plugin and place the payload into the .jar files. This is done with Bytecode manipulation. 
The plugins will then connect to the TCP Servers and you can do whatever you want with them. There is also an detection how many instances are running, so only one Client will connect at the time.
