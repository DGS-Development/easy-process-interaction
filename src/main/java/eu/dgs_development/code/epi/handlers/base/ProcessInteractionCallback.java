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

/**
 * Base class to interact with a started process.
 */
public interface ProcessInteractionCallback {
    /**
     * Requests the process to stop, by sending a termination request. It allows the process to shut down cleanly.
     */
    void destroy();

    /**
     * Kills the process forcibly. The process will terminate immediately.
     */
    void destroyForcibly();
}
