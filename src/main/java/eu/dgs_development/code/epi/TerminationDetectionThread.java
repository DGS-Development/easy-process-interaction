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

import java.util.LinkedList;
import java.util.List;

/**
 * Helper class to detect if a started process was terminated.
 */
final class TerminationDetectionThread extends Thread {
    /**
     * Callback to notify a listener that the process was terminated.
     */
    public interface ProcessListenerCallback {
        /**
         * Function to notify a listener that a process was terminated.
         * @param process The terminated process.
         */
        void onProcessFinished(Process process);
    }

    private final Process process;
    private final List<ProcessListenerCallback> processListenerCallbacks;

    /**
     * Starts the detection for the given process.
     * @param process The process to monitor.
     * @param processListenerCallback The listener to notify if the process terminated.
     */
    public TerminationDetectionThread(Process process, ProcessListenerCallback processListenerCallback) {
        List<ProcessListenerCallback> processListenerCallbacks = new LinkedList<>();
        processListenerCallbacks.add(processListenerCallback);

        this.process = process;
        this.processListenerCallbacks = processListenerCallbacks;
    }

    /**
     * Starts the detection for the given process.
     * @param process The process to monitor.
     * @param processListenerCallbacks The listeners to notify if the process terminated.
     */
    public TerminationDetectionThread(Process process, List<ProcessListenerCallback> processListenerCallbacks) {
        this.process = process;
        this.processListenerCallbacks = processListenerCallbacks;
    }

    /**
     * Returns the monitored process.
     * @return The process to monitor.
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Function which tries to detect if a process was terminated.
     */
    public void run() {
        try {
            if(process.isAlive())
                process.waitFor();

            for(ProcessListenerCallback listener : processListenerCallbacks)
                listener.onProcessFinished(process);
        }
        catch (InterruptedException interruptedException) {
            interrupt();
        }
    }

    /** Adds a process listener, to indicate the termination of the monitored process.
     * @param listener The listener to add.
     */
    public void addProcessListener(ProcessListenerCallback listener) {
        processListenerCallbacks.add(listener);
    }

    /** Removes a process listener.
     * @param listener The listener to remove.
     */
    public void removeProcessListener(ProcessListenerCallback listener) {
        processListenerCallbacks.remove(listener);
    }
}