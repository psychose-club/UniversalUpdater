/*
 * Copyright © 2022 psychose.club
 * Discord: https://www.psychose.club/discord
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package club.psychose.universalupdater;

import club.psychose.universalupdater.core.system.managers.FileManager;
import club.psychose.universalupdater.core.updater.UpdateDownloader;
import club.psychose.universalupdater.utils.Constants;
import club.psychose.universalupdater.utils.ProcessUtils;
import club.psychose.universalupdater.utils.StringUtils;
import club.psychose.universalupdater.utils.hash.HashUtils;
import club.psychose.universalupdater.utils.logging.ConsoleLogger;
import club.psychose.universalupdater.utils.logging.CrashLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/*
 * This is the main class of the application.
 */

public final class UniversalUpdater {
    // Initialize the file manager.
    public static final FileManager FILE_MANAGER = new FileManager();

    // This is the main method of the application.
    public static void main (String[] arguments) {
        System.out.println("""
                             _                          _                 _       _           \s
                 /\\ /\\ _ __ (_)_   _____ _ __ ___  __ _| |/\\ /\\ _ __   __| | __ _| |_ ___ _ __\s
                / / \\ \\ '_ \\| \\ \\ / / _ \\ '__/ __|/ _` | / / \\ \\ '_ \\ / _` |/ _` | __/ _ \\ '__|
                \\ \\_/ / | | | |\\ V /  __/ |  \\__ \\ (_| | \\ \\_/ / |_) | (_| | (_| | ||  __/ |  \s
                 \\___/|_| |_|_| \\_/ \\___|_|  |___/\\__,_|_|\\___/| .__/ \\__,_|\\__,_|\\__\\___|_|  \s
                                                               |_|                            \s
                """);
        ConsoleLogger.debug("Copyright © 2022 psychose.club");
        ConsoleLogger.debug("Version: " + Constants.VERSION);
        ConsoleLogger.debug("Build Version: " + Constants.BUILD);
        ConsoleLogger.printEmptyLine();

        // Checks if more than 6 arguments are provided.
        if (arguments.length >= 6) {
            // Options.
            boolean nextArgumentPID = false;
            boolean nextArgumentUpdateURL = false;
            boolean nextArgumentUpdateChecksum = false;
            boolean nextArgumentApplicationFilePath = false;
            boolean launch = true;
            boolean showOutput = false;

            // Default values.
            long pid = -1;
            String updateURL = null;
            String updateSHA256Checksum = null;
            Path applicationFilePath = null;
            StringBuilder argumentsStringBuilder = new StringBuilder();

            // Here we'll check the arguments.
            for (String argument : arguments) {
                if (nextArgumentPID) {
                    nextArgumentPID = false;

                    try {
                        pid = Long.parseLong(argument);
                    } catch (NumberFormatException numberFormatException) {
                        ConsoleLogger.debug("ERROR! Can't parse PID!");
                        System.exit(1);
                    }
                } else if (nextArgumentUpdateURL) {
                    nextArgumentUpdateURL = false;
                    nextArgumentUpdateChecksum = true;

                    updateURL = argument;
                } else if (nextArgumentUpdateChecksum) {
                    nextArgumentUpdateChecksum = false;

                    updateSHA256Checksum = argument;
                } else if (nextArgumentApplicationFilePath) {
                    nextArgumentApplicationFilePath = false;

                    applicationFilePath = StringUtils.getOSPath(Paths.get(argument));

                    if (!(Files.exists(applicationFilePath))) {
                        applicationFilePath = null;
                        ConsoleLogger.debug("ERROR! We didn't find the application file, please check the path!");
                    }
                } else {
                    switch (argument.toLowerCase()) {
                        case "--pid" -> nextArgumentPID = true;
                        case "--update-url" -> nextArgumentUpdateURL = true;
                        case "--application-file" -> nextArgumentApplicationFilePath = true;
                        case "--no-launch" -> launch = false;
                        case "--show-output" -> showOutput = true;
                        default -> argumentsStringBuilder.append(argument).append(" ");
                    }
                }
            }

            // Checks if the arguments are valid.
            if ((pid != -1) && (updateURL != null) && (updateSHA256Checksum != null) && (applicationFilePath != null)) {
                String argumentsString = "";

                // Checks if additional arguments are provided for the application launch.
                if (argumentsStringBuilder.length() > 0)
                    argumentsString = argumentsStringBuilder.substring(0, argumentsStringBuilder.length() - 1).trim();

                // Runs the updater.
                new UniversalUpdater().runUniversalUpdater(pid, applicationFilePath, updateURL, updateSHA256Checksum, argumentsString, launch, showOutput);
            } else {
                ConsoleLogger.debug("ERROR! Invalid arguments!");
            }
        } else {
            ConsoleLogger.debug("ERROR! Invalid arguments! Length: " + arguments.length);
        }
    }

    // This method runs the updater.
    private void runUniversalUpdater (long applicationPID, Path applicationFilePath, String updateURL, String updateSHA256Checksum, String arguments, boolean launch, boolean showOutput) {
        // Information.
        ConsoleLogger.debug("Entered arguments:");
        ConsoleLogger.debug("PID: " + applicationPID);
        ConsoleLogger.debug("Application File Path: " + applicationFilePath);
        ConsoleLogger.debug("Update URL: " + updateURL);
        ConsoleLogger.debug("Update SHA-256 Checksum: " + updateSHA256Checksum);

        if (!(arguments.isEmpty())) {
            String[] parsedArguments = arguments.split(" ");

            if (parsedArguments.length != 0) {
                ConsoleLogger.debug("Launch Arguments:");
                Arrays.stream(parsedArguments).map(argument -> "\t" + argument).forEachOrdered(ConsoleLogger::debug);
            }
        }

        ConsoleLogger.printEmptyLine();

        // Clears the temp folder.
        FILE_MANAGER.clearTempFolder();

        // Checks if the process is still alive.
        // Give the process 25 seconds to shut down.
        int processAliveAttempts = 0;
        while (ProcessUtils.isProcessAlive(applicationPID)) {
            if (processAliveAttempts == 5)
                break;

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
            } catch (InterruptedException interruptedException) {
                CrashLog.saveLogAsCrashLog(interruptedException);
            }

            processAliveAttempts ++;
        }

        // Checks if the process is not alive anymore.
        if (processAliveAttempts != 5) {
            UpdateDownloader updateDownloader = new UpdateDownloader();
            String fileName = applicationFilePath.getFileName().toString();

            ConsoleLogger.debug("Downloading update...");

            boolean success = false;
            try {
                if (updateDownloader.downloadFile(updateURL, Constants.getUniversalUpdaterFolderPath("\\temp\\" + fileName))) {
                    success = true;
                }
            } catch (IOException ioException) {
                CrashLog.saveLogAsCrashLog(ioException);
            }

            if (success) {
                ConsoleLogger.debug("Update downloaded successfully!");
                ConsoleLogger.debug("Verifying update...");

                // Calculates file checksum.
                String fileChecksum = HashUtils.calculateSHA256Hash(Constants.getUniversalUpdaterFolderPath("\\temp\\" + fileName));

                // Checks if the checksum is equals to the provided checksum.
                if ((!(fileChecksum.equals("N/A")) && (fileChecksum.equals(updateSHA256Checksum)))) {
                    ConsoleLogger.debug("Update verified successfully!");

                    // Installing the update.
                    ConsoleLogger.debug("Installing...");

                    success = false;
                    try {
                        // Deletes the old file.
                        Files.deleteIfExists(applicationFilePath);
                        success = true;
                    } catch (IOException ioException) {
                        CrashLog.saveLogAsCrashLog(ioException);
                    }

                    if (success) {
                        success = false;
                        try {
                            // Moves the update file to the old location.
                            Files.move(Constants.getUniversalUpdaterFolderPath("\\temp\\" + fileName), applicationFilePath);
                            success = true;
                        } catch (IOException ioException) {
                            CrashLog.saveLogAsCrashLog(ioException);
                        }

                        if (success) {
                            ConsoleLogger.debug("SUCCESS! Update successfully installed!");

                            // Checks if the application should be launched.
                            if (launch) {
                                ConsoleLogger.debug("Launching...");

                                // Checks if the filename has a dot to fetch the file extension.
                                if (fileName.contains(".")) {
                                    // Fetches the file extension.
                                    String fileExtension = fileName.split("\\.")[1].trim();

                                    try {
                                        // Trys to launch the application.
                                        ProcessBuilder processBuilder = null;

                                        switch (fileExtension.toLowerCase()) {
                                            case "exe", "bat", "cmd" -> processBuilder = new ProcessBuilder(applicationFilePath.toString(), arguments);
                                            case "jar" -> processBuilder = new ProcessBuilder("java", "-jar", applicationFilePath.toString(), arguments);
                                            case "sh" -> processBuilder = new ProcessBuilder("sh", applicationFilePath.toString(), arguments);
                                        }

                                        if (processBuilder != null) {
                                            processBuilder.directory(StringUtils.getOSPath(Paths.get(applicationFilePath.toString().replace(fileName, ""))).toFile());
                                            Process process = processBuilder.start();

                                            if (showOutput)
                                                System.out.println(new String(process.getInputStream().readAllBytes()));

                                            System.exit(0);
                                        } else {
                                            ConsoleLogger.debug("ERROR! Can't detect file format! Format: " + fileExtension.toLowerCase());
                                        }
                                    } catch (IOException ioException) {
                                        CrashLog.saveLogAsCrashLog(ioException);
                                    }
                                } else {
                                    ConsoleLogger.debug("ERROR! Can't detect file format!");
                                }
                            }
                        } else {
                            ConsoleLogger.debug("ERROR! Something went wrong while installing the update, update can't be moved!");
                        }
                    } else {
                        ConsoleLogger.debug("ERROR! Something went wrong while installing the update, old file can't be deleted!");
                    }
                } else {
                    ConsoleLogger.debug("ERROR! Update verification failed!");
                }
            } else {
                ConsoleLogger.debug("ERROR! Something went wrong while downloading the update!");
            }
        } else {
            ConsoleLogger.debug("ERROR! The process with the PID \"" + applicationPID + "\" seems not to shutdown!");
            ConsoleLogger.debug("ERROR! The shutdown is required to installing the update!");
        }
    }
}