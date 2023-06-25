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

package eu.dgs_development.code.epi.handlers.binary;

import eu.dgs_development.code.epi.handlers.base.ProcessHandler;

/**
 * A {@link ProcessHandler} class, specialized to read and write binary data from/to a started process.
 */
public abstract class BinaryProcessHandler extends ProcessHandler {
    /**
     * Gets called after the process was started.
     * @param binaryProcessCallback The callback to interact with the process.
     */
    public abstract void onInitialized(BinaryProcessCallback binaryProcessCallback);

    /**
     * Gets called after new bytes, from the standard output stream of the process, were read.
     * @param binaryProcessCallback The callback to interact with the process.
     * @param readBytes The amount of read bytes inside the array.
     * @param byteArray The array containing the read bytes.
     */
    public abstract void onStdBytesRead(BinaryProcessCallback binaryProcessCallback, int readBytes, byte[] byteArray);

    /**
     * Gets called after new bytes, from the error output stream of the process, were read.
     * @param binaryProcessCallback The callback to interact with the process.
     * @param readBytes The amount of read bytes inside the array.
     * @param byteArray The array containing the read bytes.
     */
    public abstract void onErrorBytesRead(BinaryProcessCallback binaryProcessCallback, int readBytes, byte[] byteArray);

    /**
     * Returns the buffer size to use, while reading bytes from the process streams.
     * @return The buffer size.
     */
    public int getBufferSize() {
        return 2048;
    }
}