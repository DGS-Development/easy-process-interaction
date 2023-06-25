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

package eu.dgs_development.code.epi.handlers.text;

import eu.dgs_development.code.epi.handlers.base.ProcessHandler;

import java.util.List;

/**
 * A {@link ProcessHandler} class, specialized to read and write text from/to a started process.
 */
public abstract class TextProcessHandler extends ProcessHandler {
    /**
     * Gets called after the process was started.
     * @param textProcessCallback The callback to interact with the process.
     */
    public abstract void onInitialized(TextProcessCallback textProcessCallback);

    /**
     * Gets called after new Unicode lines, from the standard output stream of the process, were read.
     * @param textProcessCallback The callback to interact with the process.
     * @param readLines The read lines from std-out.
     */
    public void onStdLinesRead(TextProcessCallback textProcessCallback, List<String> readLines) {
        readLines.forEach(tmpLine -> onStdLineRead(textProcessCallback, tmpLine));
    }

    /**
     * Gets called after new Unicode lines, from the error output stream of the process, were read.
     * @param textProcessCallback The callback to interact with the process.
     * @param readLines The read lines from err-out.
     */
    public void onErrorLinesRead(TextProcessCallback textProcessCallback, List<String> readLines) {
        readLines.forEach(tmpLine -> onErrorLineRead(textProcessCallback, tmpLine));
    }

    /**
     * Gets called after a new Unicode line, from the standard output stream of the process, was read.
     * @param textProcessCallback The callback to interact with the process.
     * @param readLine The read line from std-out.
     */
    public abstract void onStdLineRead(TextProcessCallback textProcessCallback, String readLine);

    /**
     * Gets called after a new Unicode line, from the error output stream of the process, was read.
     * @param textProcessCallback The callback to interact with the process.
     * @param readLine The read line from err-out.
     */
    public abstract void onErrorLineRead(TextProcessCallback textProcessCallback, String readLine);
}