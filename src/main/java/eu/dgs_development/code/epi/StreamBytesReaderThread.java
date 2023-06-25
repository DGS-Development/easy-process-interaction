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

import java.io.*;

/**
 * Helper class to read all available bytes from a process {@link InputStream} and notify a {@link StreamCallback}
 * listener about the available data.
 */
final class StreamBytesReaderThread extends Thread {
    /**
     * A callback to notify a listener about read bytes from a process or an occurred {@link IOException}.
     */
    public interface StreamCallback {
        /**
         * Function which is called if a process-exception occurs.
         * @param ioException The {@link IOException} that occurred.
         */
        void onIOException(IOException ioException);

        /**
         * Function which is called if bytes from a process were read.
         * @param readBytes The amount of read bytes from the standard output stream.
         * @param byteArray The array containing at least the read bytes.
         */
        void onBytesRead(int readBytes, byte[] byteArray);
    }

    private final InputStream inputStream;
    private final int bufferSize;
    private final StreamBytesReaderThread.StreamCallback streamCallback;

    /**
     * Creates a new {@link StreamBytesReaderThread} instance.
     * @param inputStream The process input stream to read bytes from.
     * @param bufferSize The internal buffer size to fill until a certain amount of bytes was read.
     * @param streamCallback The {@link StreamCallback} to notify if data bytes were read or an error occurs.
     */
    public StreamBytesReaderThread(InputStream inputStream, int bufferSize,
                                   StreamBytesReaderThread.StreamCallback streamCallback) {
        this.inputStream = inputStream;
        this.bufferSize = bufferSize;
        this.streamCallback = streamCallback;
    }

    /**
     * Function which tries to read all bytes from the standard input stream of a started process.
     */
    @Override
    public void run() {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, bufferSize);

            int readBytes;
            byte[] buffer = new byte[bufferSize];

            while (!isInterrupted() && (readBytes = bufferedInputStream.read(buffer)) != -1)
                streamCallback.onBytesRead(readBytes, buffer);

            bufferedInputStream.close();
        }
        catch (IOException ioException) {
            streamCallback.onIOException(ioException);
        }
    }
}