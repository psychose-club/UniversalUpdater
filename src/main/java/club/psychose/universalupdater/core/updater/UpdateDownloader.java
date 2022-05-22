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

package club.psychose.universalupdater.core.updater;

import club.psychose.universalupdater.utils.logging.CrashLog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;

/*
 * This class downloads the update.
 */

public final class UpdateDownloader {
    // This method downloads the update file.
    public boolean downloadFile (String downloadURL, Path outputPath) throws IOException {
        // Setup URL connection.
        URL url = new URL(downloadURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        // Check if the response is 200.
        if (httpURLConnection.getResponseCode() == 200) {
            boolean exceptionThrown = false;

            // The BufferedInputStream tries to read the content.
            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream())) {
                // Sets the file output,
                try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath.toFile())) {
                    // Writes the bytes.
                    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                        int bufferLength = 1024;

                        byte[] data = new byte[bufferLength];

                        int readBytes;
                        while ((readBytes = bufferedInputStream.read(data, 0, bufferLength)) != -1) {
                            bufferedOutputStream.write(data, 0, readBytes);
                            bufferedOutputStream.flush();
                        }
                    } catch (Exception exception) {
                        CrashLog.saveLogAsCrashLog(exception);
                        exceptionThrown = true;
                    }
                } catch (Exception exception) {
                    CrashLog.saveLogAsCrashLog(exception);
                    exceptionThrown = true;
                }
            } catch (Exception exception) {
                CrashLog.saveLogAsCrashLog(exception);
                exceptionThrown = true;
            }

            return !(exceptionThrown);
        }

        return false;
    }
}