/*
Copyright 2023 DGS-Development (https://github.com/DGS-Development)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package eu.dgs_development.code.epi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Helper class to read all available lines from a process {@link InputStream} and notify a {@link StreamCallback}
 * listener about the available text.
 */
final class StreamTextReaderThread extends Thread {
    /**
     * A callback to notify a listener about read line from a process or an occurred {@link IOException}.
     */
    public interface StreamCallback {
        /**
         * Function which is called if a process-exception occurs.
         * @param ioException The {@link IOException} that occurred.
         */
        void onIOException(IOException ioException);

        /**
         * Function which is called if a line from a process was read.
         * @param line The read line.
         */
        void onLineRead(String line);
    }

    private final InputStream inputStream;
    private final StreamCallback streamCallback;

    /**
     * Creates a new {@link StreamTextReaderThread} instance.
     * @param inputStream The process input stream to read bytes from.
     * @param streamCallback The {@link StreamCallback} to notify if lines were read or an error occurs.
     */
    public StreamTextReaderThread(InputStream inputStream, StreamCallback streamCallback) {
        this.inputStream = inputStream;
        this.streamCallback = streamCallback;
    }

    /**
     * Function which tries to read all lines from the standard input stream of a started process.
     */
    @Override
    public void run() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine;

            while (!isInterrupted() && (readLine = bufferedReader.readLine()) != null)
                streamCallback.onLineRead(readLine);

            bufferedReader.close();
            inputStreamReader.close();
        }
        catch (IOException ioException) {
            streamCallback.onIOException(ioException);
        }
    }
}