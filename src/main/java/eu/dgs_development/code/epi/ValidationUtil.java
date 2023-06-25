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

import java.io.File;

/**
 * Utility class to perform various validation checks.
 */
final class ValidationUtil {
    /**
     * Function that checks if a parameter is null.
     * @param parameter The parameter to check.
     * @param parameterName The name of the parameter to check.
     */
    public static void checkParameterNotNull(Object parameter, String parameterName) {
        if(parameter == null)
            throw new IllegalArgumentException("The parameter \"" + parameterName + "\" can't be null.");
    }

    /**
     * Function to check if a directory exists.
     * @param directory The directory to check.
     * @param parameterName The name of the parameter to check.
     */
    public static void checkDirectoryIsValid(File directory, String parameterName) {
        checkParameterNotNull(directory, parameterName);

        if(!directory.isDirectory())
            throw new IllegalArgumentException("The parameter \"" + parameterName + "\" isn't a directory. " +
                    "Path: " + directory.getAbsolutePath());
    }

    /**
     * Function to check if a file exists.
     * @param file The file to check.
     * @param parameterName The name of the parameter to check.
     */
    public static void checkFileIsValid(File file, String parameterName) {
        checkParameterNotNull(file, parameterName);

        if(!file.isFile())
            throw new IllegalArgumentException("The parameter \"" + parameterName + "\" isn't a file. " +
                    "Path: " + file.getAbsolutePath());
    }
}