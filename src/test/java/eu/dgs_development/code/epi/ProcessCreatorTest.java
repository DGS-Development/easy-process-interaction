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

import eu.dgs_development.code.epi.handlers.binary.BinaryProcessCallback;
import eu.dgs_development.code.epi.handlers.binary.BinaryProcessHandler;
import eu.dgs_development.code.epi.handlers.text.TextProcessCallback;
import eu.dgs_development.code.epi.handlers.text.TextProcessHandler;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProcessCreatorTest {
    private static File executableFile;

    private static String getExecutablePathOrNull() {
        String osName = System.getProperty("os.name").toLowerCase();

        if(osName.contains("win")) {
            return "go-echo/bin/go-echo-amd64.exe";
        }
        else if(osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return "go-echo/bin/go-echo-amd64-linux";
        }
        else if(osName.contains("mac")) {
            return "go-echo/bin/go-echo-amd64-darwin";
        }

        return null;
    }

    @BeforeAll
    public static void setup(){
        //Check if native tests should be performed.
        File testTriggerFile = new File("TRIGGER_TESTS");

        Assumptions.assumeTrue(testTriggerFile.isFile(), "Unable to find test trigger file: Invalid path \"" +
                testTriggerFile.getAbsolutePath() + "\".");

        //Try to set the correct executable.
        String executablePath = getExecutablePathOrNull();

        if(executablePath == null) {
            Assertions.fail("Unknown OS: Unable to detect the correct executable.");
        }

        executableFile = new File(getExecutablePathOrNull());

        Assumptions.assumeTrue(executableFile.isFile(), "Unable to find test executable: Invalid path \"" +
                executableFile.getAbsolutePath() + "\".");
    }

    @Test
    @Order(1)
    public void processWorkingDirectoryTest() {
        List<String> arguments = new ArrayList<>();
        arguments.add("pwd");

        AtomicBoolean testCompleted = new AtomicBoolean(false);

        final String[] lastReadString = {null};

        final Throwable[] throwable = {null};

        ProcessCreator.startProcessInProcessDirectory(executableFile, arguments, new TextProcessHandler() {
            {
                Thread.currentThread().setUncaughtExceptionHandler((thread, unexpectedThrowable) ->
                        throwable[0] = unexpectedThrowable);
            }

            @Override
            public void onInitialized(TextProcessCallback textProcessCallback) {
                //Ignore...
            }

            @Override
            public void onStdLineRead(TextProcessCallback textProcessCallback, String readLine) {
                lastReadString[0] = readLine;
            }

            @Override
            public void onErrorLineRead(TextProcessCallback textProcessCallback, String readLine) {
                try {
                    Assertions.fail("Read unexpected line from stderr: " + readLine);
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }
            }

            @Override
            public void onProcessExited(int exitCode) {
                try {
                    Assertions.assertEquals(0, exitCode, "Unexpected exit code.");
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }

                testCompleted.set(true);
            }

            @Override
            public void onIOException(IOException ioException) {
                try {
                    Assertions.fail("Unexpected IO exception.", ioException);
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }
            }
        });

        while (!testCompleted.get()) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException interruptedException) {
                Assertions.fail("Unexpected interrupted exception.", interruptedException);
            }
        }

        if(throwable[0] != null) {
            Assertions.fail("Unexpected assertion-exception.", throwable[0]);
        }

        Assertions.assertEquals(executableFile.getParentFile().getAbsolutePath(), lastReadString[0],
                "Unexpected working directory.");
    }

    @Test
    @Order(2)
    public void customWorkingDirectoryTest() {
        File tmpWorkingDirectoryFile = new File(System.getProperty("java.io.tmpdir"));

        List<String> arguments = new ArrayList<>();
        arguments.add("pwd");

        AtomicBoolean testCompleted = new AtomicBoolean(false);

        final String[] lastReadString = {null};

        final Throwable[] throwable = {null};

        ProcessCreator.startProcess(executableFile, tmpWorkingDirectoryFile, arguments, new TextProcessHandler() {
            {
                Thread.currentThread().setUncaughtExceptionHandler((thread, unexpectedThrowable) ->
                        throwable[0] = unexpectedThrowable);
            }

            @Override
            public void onInitialized(TextProcessCallback textProcessCallback) {
                //Ignore...
            }

            @Override
            public void onStdLineRead(TextProcessCallback textProcessCallback, String readLine) {
                lastReadString[0] = readLine;
            }

            @Override
            public void onErrorLineRead(TextProcessCallback textProcessCallback, String readLine) {
                try {
                    Assertions.fail("Read unexpected line from stderr: " + readLine);
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }
            }

            @Override
            public void onProcessExited(int exitCode) {
                try {
                    Assertions.assertEquals(0, exitCode, "Unexpected exit code.");
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }

                testCompleted.set(true);
            }

            @Override
            public void onIOException(IOException ioException) {
                try {
                    Assertions.fail("Unexpected IO exception.", ioException);
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }
            }
        });

        while (!testCompleted.get()) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException interruptedException) {
                Assertions.fail("Unexpected interrupted exception.", interruptedException);
            }
        }

        if(throwable[0] != null) {
            Assertions.fail("Unexpected assertion-exception.", throwable[0]);
        }

        Assertions.assertEquals(tmpWorkingDirectoryFile.getAbsolutePath(), lastReadString[0],
                "Unexpected working directory.");
    }

    @Test
    @Order(3)
    public void stringStdoutEchoTest() throws NoSuchAlgorithmException {
        final String testString = "This is a test string to echo from the started process!";
        final String exitString = "EXIT";

        MessageDigest outMessageDigest = MessageDigest.getInstance("SHA-256");
        MessageDigest inMessageDigest = MessageDigest.getInstance("SHA-256");

        List<String> arguments = new ArrayList<>();
        arguments.add("echo-string");

        AtomicBoolean testCompleted = new AtomicBoolean(false);

        final Throwable[] throwable = {null};

        ProcessCreator.startProcessInProcessDirectory(executableFile, arguments, new TextProcessHandler() {
            {
                Thread.currentThread().setUncaughtExceptionHandler((thread, unexpectedThrowable) ->
                        throwable[0] = unexpectedThrowable);
            }

            @Override
            public void onInitialized(TextProcessCallback textProcessCallback) {
                try {
                    //Send three test strings.

                    textProcessCallback.writeLine(testString + "\n");
                    outMessageDigest.update(testString.getBytes(StandardCharsets.UTF_8));

                    textProcessCallback.writeLine(testString + "\n");
                    outMessageDigest.update(testString.getBytes(StandardCharsets.UTF_8));

                    textProcessCallback.writeLine(testString + "\n");
                    outMessageDigest.update(testString.getBytes(StandardCharsets.UTF_8));

                    //Send exit string to terminate the process.

                    textProcessCallback.writeLine(exitString + "\n");
                }
                catch (IOException ioException) {
                    try {
                        Assertions.fail("Unexpected IO exception.", ioException);
                    }
                    catch (Throwable unexpectedThrowable) {
                        throwable[0] = unexpectedThrowable;
                    }
                }
            }

            @Override
            public void onStdLineRead(TextProcessCallback textProcessCallback, String readLine) {
                inMessageDigest.update(readLine.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public void onErrorLineRead(TextProcessCallback textProcessCallback, String readLine) {
                try {
                    Assertions.fail("Read unexpected line from stderr: " + readLine);
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }
            }

            @Override
            public void onProcessExited(int exitCode) {
                try {
                    Assertions.assertEquals(0, exitCode, "Unexpected exit code.");
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }

                testCompleted.set(true);
            }

            @Override
            public void onIOException(IOException ioException) {
                try {
                    Assertions.fail("Unexpected IO exception.", ioException);
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }
            }
        });

        while (!testCompleted.get()) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException interruptedException) {
                Assertions.fail("Unexpected interrupted exception.", interruptedException);
            }
        }

        if(throwable[0] != null) {
            Assertions.fail("Unexpected assertion-exception.", throwable[0]);
        }

        Assertions.assertArrayEquals(outMessageDigest.digest(), inMessageDigest.digest(),
                "Unexpected hash value: The received data is not equal to the transmitted data.");
    }

    @Test
    @Order(4)
    public void bytesStdoutEchoTest() throws NoSuchAlgorithmException {
        final byte[] testBytes = {2, 4, 6, 8};
        final byte[] exitBytes = {69, 88, 73, 84};

        MessageDigest outMessageDigest = MessageDigest.getInstance("SHA-256");
        MessageDigest inMessageDigest = MessageDigest.getInstance("SHA-256");

        List<String> arguments = new ArrayList<>();
        arguments.add("echo-bytes");

        AtomicBoolean testCompleted = new AtomicBoolean(false);

        final Throwable[] throwable = {null};

        ProcessCreator.startProcessInProcessDirectory(executableFile, arguments, new BinaryProcessHandler() {
            {
                Thread.currentThread().setUncaughtExceptionHandler((thread, unexpectedThrowable) ->
                        throwable[0] = unexpectedThrowable);
            }

            @Override
            public void onInitialized(BinaryProcessCallback binaryProcessCallback) {
                try {
                    //Send three test arrays.

                    binaryProcessCallback.writeBytes(testBytes);
                    outMessageDigest.update(testBytes);

                    binaryProcessCallback.writeBytes(testBytes);
                    outMessageDigest.update(testBytes);

                    binaryProcessCallback.writeBytes(testBytes);
                    outMessageDigest.update(testBytes);

                    //Send exit bytes to terminate the process.

                    binaryProcessCallback.writeBytes(exitBytes);
                }
                catch (IOException ioException) {
                    try {
                        Assertions.fail("Unexpected IO exception.", ioException);
                    }
                    catch (Throwable unexpectedThrowable) {
                        throwable[0] = unexpectedThrowable;
                    }
                }
            }

            @Override
            public void onStdBytesRead(BinaryProcessCallback binaryProcessCallback, int readBytes, byte[] byteArray) {
                byte[] readBytesArray = Arrays.copyOf(byteArray, readBytes);

                inMessageDigest.update(readBytesArray);
            }

            @Override
            public void onErrorBytesRead(BinaryProcessCallback binaryProcessCallback, int readBytes, byte[] byteArray) {
                byte[] readBytesArray = Arrays.copyOf(byteArray, readBytes);

                try {
                    Assertions.fail("Read unexpected bytes from stderr: " + Arrays.toString(readBytesArray));
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }
            }

            @Override
            public void onProcessExited(int exitCode) {
                try {
                    Assertions.assertEquals(0, exitCode, "Unexpected exit code.");
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }

                testCompleted.set(true);
            }

            @Override
            public void onIOException(IOException ioException) {
                try {
                    Assertions.fail("Unexpected IO exception.", ioException);
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }
            }
        });

        while (!testCompleted.get()) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException interruptedException) {
                Assertions.fail("Unexpected interrupted exception.", interruptedException);
            }
        }

        if(throwable[0] != null) {
            Assertions.fail("Unexpected assertion-exception.", throwable[0]);
        }

        Assertions.assertArrayEquals(outMessageDigest.digest(), inMessageDigest.digest(),
                "Unexpected hash value: The received data is not equal to the transmitted data.");
    }

    @Test
    @Order(5)
    public void bytesStderrEchoTest() throws NoSuchAlgorithmException {
        final byte[] testBytes = {2, 4, 6, 8};
        final byte[] exitBytes = {69, 88, 73, 84};

        MessageDigest outMessageDigest = MessageDigest.getInstance("SHA-256");
        MessageDigest inMessageDigest = MessageDigest.getInstance("SHA-256");

        List<String> arguments = new ArrayList<>();
        arguments.add("echo-bytes-stderr");

        AtomicBoolean testCompleted = new AtomicBoolean(false);

        final Throwable[] throwable = {null};

        ProcessCreator.startProcessInProcessDirectory(executableFile, arguments, new BinaryProcessHandler() {
            {
                Thread.currentThread().setUncaughtExceptionHandler((thread, unexpectedThrowable) ->
                        throwable[0] = unexpectedThrowable);
            }

            @Override
            public void onInitialized(BinaryProcessCallback binaryProcessCallback) {
                try {
                    //Send three test arrays.

                    binaryProcessCallback.writeBytes(testBytes);
                    outMessageDigest.update(testBytes);

                    binaryProcessCallback.writeBytes(testBytes);
                    outMessageDigest.update(testBytes);

                    binaryProcessCallback.writeBytes(testBytes);
                    outMessageDigest.update(testBytes);

                    //Send exit bytes to terminate the process.

                    binaryProcessCallback.writeBytes(exitBytes);
                }
                catch (IOException ioException) {
                    Assertions.fail("Unexpected IO exception.", ioException);
                }
            }

            @Override
            public void onStdBytesRead(BinaryProcessCallback binaryProcessCallback, int readBytes, byte[] byteArray) {
                byte[] readBytesArray = Arrays.copyOf(byteArray, readBytes);

                Assertions.fail("Read unexpected bytes from stderr: " + Arrays.toString(readBytesArray));
            }

            @Override
            public void onErrorBytesRead(BinaryProcessCallback binaryProcessCallback, int readBytes, byte[] byteArray) {
                byte[] readBytesArray = Arrays.copyOf(byteArray, readBytes);

                inMessageDigest.update(readBytesArray);
            }

            @Override
            public void onProcessExited(int exitCode) {
                Assertions.assertEquals(0, exitCode, "Unexpected exit code.");

                testCompleted.set(true);
            }

            @Override
            public void onIOException(IOException ioException) {
                Assertions.fail("Unexpected IO exception.", ioException);
            }
        });

        while (!testCompleted.get()) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException interruptedException) {
                Assertions.fail("Unexpected interrupted exception.", interruptedException);
            }
        }

        if(throwable[0] != null) {
            Assertions.fail("Unexpected assertion-exception.", throwable[0]);
        }

        Assertions.assertArrayEquals(outMessageDigest.digest(), inMessageDigest.digest(),
                "Unexpected hash value: The received data is not equal to the transmitted data.");
    }
}
