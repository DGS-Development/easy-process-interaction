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

import eu.dgs_development.code.epi.handlers.text.TextProcessCallback;
import eu.dgs_development.code.epi.handlers.text.TextProcessHandler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommandLineExecutorTest {
    @BeforeAll
    public static void setup() {
        //Check if native tests should be performed.
        File testTriggerFile = new File("TRIGGER_TESTS");

        Assumptions.assumeTrue(testTriggerFile.isFile(), "Unable to find test trigger file: Invalid path \"" +
                testTriggerFile.getAbsolutePath() + "\".");
    }

    @Test
    @Order(0)
    @EnabledOnOs({OS.LINUX, OS.MAC})
    public void executeShCommandUnixTest() {
        //sh should always be supported.
        readWhoamiUsername(CommandLineExecutor.CommandLineType.UNIX_SH);
    }

    @Test
    @Order(1)
    @EnabledOnOs(OS.WINDOWS)
    public void executeCmdCommandWindowsTest() {
        //CMD should always be supported.
        readWhoamiUsername(CommandLineExecutor.CommandLineType.WINDOWS_CMD);
    }

    @Test
    @Order(2)
    @EnabledOnOs(OS.WINDOWS)
    public void executePowershellCommandWindowsTest() {
        //Powershell should always be supported.
        readWhoamiUsername(CommandLineExecutor.CommandLineType.WINDOWS_POWERSHELL_64BIT);
    }

    public static void readWhoamiUsername(CommandLineExecutor.CommandLineType commandLineType) {
        AtomicBoolean testCompleted = new AtomicBoolean(false);

        final Throwable[] throwable = {null};

        //Try to execute an "actual" command.
        //The "whoami" command should be supported on Windows and Unix-like systems.
        CommandLineExecutor.executeCommand(commandLineType, "whoami", new TextProcessHandler() {
            @Override
            public void onInitialized(TextProcessCallback textProcessCallback) {
                //Ignore, because we don't need to send data or kill the process...
            }

            @Override
            public void onStdLineRead(TextProcessCallback textProcessCallback, String readLine) {
                try {
                    Assertions.assertFalse(readLine.trim().isEmpty(), "The read username shouldn't be empty.");
                }
                catch (Throwable unexpectedThrowable) {
                    throwable[0] = unexpectedThrowable;
                }
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
    }
}
