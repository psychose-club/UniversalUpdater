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

package club.psychose.universalupdater.core.system.managers;

import club.psychose.universalupdater.utils.Constants;
import club.psychose.universalupdater.utils.logging.CrashLog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/*
 * This class manage files and creates automatically the needed directories for the application content.
 */

public final class FileManager {
    // Public constructor.
    public FileManager () {
        // Creates the directories.
        try {
            if (!(Files.exists(Constants.getUniversalUpdaterFolderPath(null))))
                Files.createDirectories(Constants.getUniversalUpdaterFolderPath(null));

            if (!(Files.exists(Constants.getUniversalUpdaterFolderPath("\\logs\\"))))
                Files.createDirectories(Constants.getUniversalUpdaterFolderPath("\\logs\\"));

            if (!(Files.exists(Constants.getUniversalUpdaterFolderPath("\\temp\\"))))
                Files.createDirectories(Constants.getUniversalUpdaterFolderPath("\\temp\\"));
        } catch (IOException ioException) {
            CrashLog.saveLogAsCrashLog(ioException);
        }
    }

    //This method clears the temp folder.
    public void clearTempFolder () {
        // Checks if the temp folder exist and fetches the folder content.
        if (Files.exists(Constants.getUniversalUpdaterFolderPath("\\temp\\"))) {
            File[] tempFiles = Constants.getUniversalUpdaterFolderPath("\\temp\\").toFile().listFiles();

            // Checks if the folder has contents available.
            if (tempFiles != null) {
                // Deletes the file.
                Arrays.stream(tempFiles).forEachOrdered(file -> {
                    try {
                        Files.deleteIfExists(file.toPath());
                    } catch (IOException ioException) {
                        CrashLog.saveLogAsCrashLog(ioException);
                    }
                });
            }
        }
    }

    // This method saves an ArrayList to a file.
    public void saveArrayListToAFile (Path outputPath, ArrayList<String> arrayListToSave) {
        // Collects the content from the ArrayList and converts it to a string.
        String contentString = arrayListToSave.stream().map(line -> line + "\n").collect(Collectors.joining());

        try {
            // Creates the file and write the string.
            Files.write(outputPath, contentString.getBytes());
        } catch (IOException ioException) {
            CrashLog.saveLogAsCrashLog(ioException);
        }
    }
}