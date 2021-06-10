package net.vitox.client.util;

import java.io.IOException;

public class FeatureUtil {

    public static void shutdown() throws RuntimeException, IOException {
        String shutdownCommand;
        String operatingSystem = System.getProperty("os.name");

        if (operatingSystem.contains("Linux") || operatingSystem.contains("Mac")) {
            shutdownCommand = "shutdown -h now";
        }
        else if (operatingSystem.contains("Windows")) {
            shutdownCommand = "shutdown.exe -s -t 0";
        }
        else {
            throw new RuntimeException("Unsupported operating system.");
        }

        Runtime.getRuntime().exec(shutdownCommand);
        System.exit(0);
    }

}
