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

import eu.dgs_development.code.epi.handlers.base.ProcessInteractionCallback;

import java.io.IOException;

/**
 * {@link ProcessInteractionCallback} class to enable the user to interact with a process of a {@link BinaryProcessHandler}.
 */
public interface BinaryProcessCallback extends ProcessInteractionCallback {
    /**
     * Writes bytes to the standard input stream of the process.
     * @param bytes The bytes to write.
     * @throws IOException Exception if an IO error occurs.
     */
    void writeBytes(byte[] bytes) throws IOException;
}
