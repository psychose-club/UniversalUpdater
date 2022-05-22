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

package club.psychose.universalupdater.utils;

import java.util.Optional;

/*
 * This class handles utilities for processes.
 */

public final class ProcessUtils {
    // This method checks if the process is alive.
    public static boolean isProcessAlive (long processID) {
        Optional<ProcessHandle> processHandle = ProcessHandle.of(processID);
        return ((processHandle.isPresent()) && (processHandle.get().isAlive()));
    }
}