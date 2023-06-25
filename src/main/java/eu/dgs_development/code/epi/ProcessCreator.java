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

import eu.dgs_development.code.epi.handlers.base.ProcessHandler;
import eu.dgs_development.code.epi.handlers.text.TextProcessCallback;
import eu.dgs_development.code.epi.handlers.text.TextProcessHandler;
import eu.dgs_development.code.epi.handlers.binary.BinaryProcessCallback;
import eu.dgs_development.code.epi.handlers.binary.BinaryProcessHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to execute processes from a given working directory.
 */
public final class ProcessCreator {
    private ProcessCreator() {
        //Ignore...
    }

    /**
     * Starts a new process by using the process directory as working directory.
     * @param processFile The process executable.
     * @param arguments The arguments to start the process with.
     * @param textProcessHandler The handler to handle all process interactions.
     */
    public static void startProcessInProcessDirectory(File processFile, List<String> arguments,
                                               TextProcessHandler textProcessHandler) {
        startProcess(processFile, processFile.getParentFile(), arguments, textProcessHandler);
    }

    /**
     * Starts a new process by using the process directory as working directory.
     * @param processFile The process executable.
     * @param arguments The arguments to start the process with.
     * @param binaryProcessHandler The handler to handle all process interactions.
     */
    public static void startProcessInProcessDirectory(File processFile, List<String> arguments,
                                               BinaryProcessHandler binaryProcessHandler) {
        startProcess(processFile, processFile.getParentFile(), arguments, binaryProcessHandler);
    }

    /**
     * Starts a new process.
     * @param processFile The process executable.
     * @param workingDirectory The working directory of the process to start.
     * @param arguments The arguments to start the process with.
     * @param textProcessHandler The handler to handle all process text interactions.
     */
    public static void startProcess(File processFile, File workingDirectory, List<String> arguments,
                             TextProcessHandler textProcessHandler) {
        startProcess(processFile, workingDirectory, arguments, (ProcessHandler) textProcessHandler);
    }

    /**
     * Starts a new process.
     * @param processFile The process executable.
     * @param workingDirectory The working directory of the process to start.
     * @param arguments The arguments to start the process with.
     * @param binaryProcessHandler The handler to handle all process binary interactions.
     */
    public static void startProcess(File processFile, File workingDirectory, List<String> arguments,
                             BinaryProcessHandler binaryProcessHandler) {
        startProcess(processFile, workingDirectory, arguments, (ProcessHandler) binaryProcessHandler);
    }

    private static void startProcess(File processFile, File workingDirectory, List<String> arguments,
                              ProcessHandler processHandler) {
        ValidationUtil.checkFileIsValid(processFile, "processFile");
        ValidationUtil.checkParameterNotNull(processHandler, "processHandler");

        if(arguments == null)
            arguments = new ArrayList<>(0);

        List<String> command = new LinkedList<>();
        command.add(processFile.getAbsolutePath());
        command.addAll(arguments);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDirectory);

        try {
            Process process = processBuilder.start();

            if(processHandler instanceof TextProcessHandler) {
                TextProcessHandler textProcessHandler = (TextProcessHandler) processHandler;

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

                TextProcessCallback textProcessCallback = new TextProcessCallback() {
                    @Override
                    public void destroy() {
                        process.destroy();
                    }

                    @Override
                    public void destroyForcibly() {
                        process.destroyForcibly();
                    }

                    @Override
                    public void writeLine(String line) throws IOException {
                        bufferedWriter.write(line);
                        bufferedWriter.flush();
                    }
                };

                textProcessHandler.onInitialized(textProcessCallback);

                TerminationDetectionThread.ProcessListenerCallback processListenerCallback = terminatedProcess -> {
                    textProcessHandler.onProcessExited(terminatedProcess.exitValue());

                    try {
                        bufferedWriter.close();
                    }
                    catch (IOException ioException) {
                        textProcessHandler.onIOException(ioException);
                    }
                };

                TerminationDetectionThread processTerminationThread = new TerminationDetectionThread(process,
                        processListenerCallback);

                processTerminationThread.start();

                StreamTextReaderThread standardReader = new StreamTextReaderThread(process.getInputStream(),
                        new StreamTextReaderThread.StreamCallback() {
                    @Override
                    public void onIOException(IOException ioException) {
                        textProcessHandler.onIOException(ioException);
                    }

                    @Override
                    public void onLineRead(String line) {
                        textProcessHandler.onStdLineRead(textProcessCallback, line);
                    }
                });

                StreamTextReaderThread errorReader = new StreamTextReaderThread(process.getErrorStream(),
                        new StreamTextReaderThread.StreamCallback() {
                    @Override
                    public void onIOException(IOException ioException) {
                        processHandler.onIOException(ioException);
                    }

                    @Override
                    public void onLineRead(String line) {
                        ((TextProcessHandler) processHandler).onErrorLineRead(textProcessCallback, line);
                    }
                });

                standardReader.start();
                errorReader.start();
            }
            else if(processHandler instanceof BinaryProcessHandler) {
                BinaryProcessHandler binaryProcessHandler = (BinaryProcessHandler) processHandler;

                BinaryProcessCallback binaryProcessCallback = new BinaryProcessCallback() {
                    @Override
                    public void destroy() {
                        process.destroy();
                    }

                    @Override
                    public void destroyForcibly() {
                        process.destroyForcibly();
                    }

                    @Override
                    public void writeBytes(byte[] bytes) throws IOException {
                        process.getOutputStream().write(bytes);
                        process.getOutputStream().flush();
                    }
                };

                binaryProcessHandler.onInitialized(binaryProcessCallback);

                TerminationDetectionThread.ProcessListenerCallback processListenerCallback = terminatedProcess ->
                        binaryProcessHandler.onProcessExited(terminatedProcess.exitValue());

                TerminationDetectionThread processTerminationThread = new TerminationDetectionThread(process,
                        processListenerCallback);

                processTerminationThread.start();

                StreamBytesReaderThread outputReader = new StreamBytesReaderThread(process.getInputStream(),
                        binaryProcessHandler.getBufferSize(),
                        new StreamBytesReaderThread.StreamCallback() {
                            @Override
                            public void onIOException(IOException ioException) {
                                binaryProcessHandler.onIOException(ioException);
                            }

                            @Override
                            public void onBytesRead(int readBytes, byte[] byteArray) {
                                binaryProcessHandler.onStdBytesRead(binaryProcessCallback, readBytes, byteArray);
                            }
                        });

                StreamBytesReaderThread errorReader = new StreamBytesReaderThread(process.getErrorStream(),
                        binaryProcessHandler.getBufferSize(),
                        new StreamBytesReaderThread.StreamCallback() {
                            @Override
                            public void onIOException(IOException ioException) {
                                binaryProcessHandler.onIOException(ioException);
                            }

                            @Override
                            public void onBytesRead(int readBytes, byte[] byteArray) {
                                binaryProcessHandler.onErrorBytesRead(binaryProcessCallback, readBytes, byteArray);
                            }
                        });

                errorReader.start();
                outputReader.start();
            }
        }
        catch (IOException ioException) {
            processHandler.onIOException(ioException);
        }
    }
}