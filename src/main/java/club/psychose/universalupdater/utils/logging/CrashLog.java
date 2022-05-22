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

package club.psychose.universalupdater.utils.logging;

import club.psychose.universalupdater.UniversalUpdater;
import club.psychose.universalupdater.utils.Constants;
import club.psychose.universalupdater.utils.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;

/*
 * This class handles the crash logging.
 */

public final class CrashLog {

    // This method saves the exception to a txt file.
    public static void saveLogAsCrashLog (Exception exception) {
        ArrayList<String> crashLogArrayList = new ArrayList<>();

        String timestamp = StringUtils.getDateAndTime("LOG");

        crashLogArrayList.add("==================================================================================================");
        crashLogArrayList.add("                                            Crash Log                                             ");
        crashLogArrayList.add("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        crashLogArrayList.add("File: " + Constants.getUniversalUpdaterFolderPath("\\logs\\" + timestamp + ".txt"));
        crashLogArrayList.add("Timestamp: " + timestamp);
        crashLogArrayList.add("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        crashLogArrayList.add("OS: " + System.getProperty("os.name"));
        crashLogArrayList.add("OS Architecture: " + System.getProperty("os.arch"));
        crashLogArrayList.add("JRE Version: " + System.getProperty("java.version"));
        crashLogArrayList.add("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        crashLogArrayList.add("Exception Cause: " + exception.getCause());
        crashLogArrayList.add("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        crashLogArrayList.add("PrintStackTrace:");
        crashLogArrayList.add(ExceptionUtils.getStackTrace(exception));
        crashLogArrayList.add("==================================================================================================");

        UniversalUpdater.FILE_MANAGER.saveArrayListToAFile(Constants.getUniversalUpdaterFolderPath("\\logs\\" + timestamp + ".txt"), crashLogArrayList);
        UniversalUpdater.FILE_MANAGER.saveArrayListToAFile(Constants.getUniversalUpdaterFolderPath("\\logs\\latest.txt"), crashLogArrayList);

        ConsoleLogger.debug("ERROR: An exception occurred! Crash Log under: " + Constants.getUniversalUpdaterFolderPath("\\logs\\" + timestamp + ".txt"));
        exception.printStackTrace();
    }
}