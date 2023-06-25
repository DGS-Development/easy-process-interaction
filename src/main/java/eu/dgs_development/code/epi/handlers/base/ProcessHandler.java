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

package eu.dgs_development.code.epi.handlers.base;

import java.io.IOException;

/**
 * Base class of all {@link ProcessHandler} classes.
 */
public abstract class ProcessHandler {
    /**
     * Gets executed when the process was terminated.
     * @param exitCode The exit code returned by the process.
     */
    public abstract void onProcessExited(int exitCode);

    /**
     * Gets executed if an IO error occurs, while reading from, or writing to, the process in- and output streams.
     * @param ioException The occurred {@link IOException}.
     */
    public abstract void onIOException(IOException ioException);
}
