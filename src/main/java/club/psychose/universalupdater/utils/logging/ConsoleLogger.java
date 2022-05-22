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

import club.psychose.universalupdater.utils.StringUtils;

/*
 * This class contains the logger for the application.
 * It prints every event that happens in the application to the console.
 */

public final class ConsoleLogger {
    // Prints an empty line in the console.
    public static void printEmptyLine () {
        System.out.println(" ");
    }

    // Prints the current debug information in the console.
    public static void debug (Object output) {
        System.out.println(StringUtils.getDateAndTime("CONSOLE") + " | [UniversalUpdater]: " + output);
    }
}