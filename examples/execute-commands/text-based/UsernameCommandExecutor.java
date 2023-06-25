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

/**
 * The following example shows how to read text from a command line process, executing the "whoami"-command.
 */
public class UsernameCommandExecutor {
    public static void main(String[] args) {
        //Try to detect the OS in order to launch the correct command line process.
        //The "user" just needs to determine if the OS is Windows or Unix-based.
        boolean osIsWindows = System.getProperty("os.name").toLowerCase().contains("win");

        UsernameCommandExecutor.readUsername(osIsWindows, new UsernameCallback() {
            @Override
            public void onUsernameRead(String username) {
                System.out.println("Read username: " + username);
            }

            @Override
            public void onException(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public interface UsernameCallback {
        void onUsernameRead(String username);

        void onException(Exception exception);
    }

    public static void readUsername(boolean osIsWindows, UsernameCallback usernameCallback) {
        CommandLineExecutor.CommandLineType commandLineType;

        if (osIsWindows) {
            commandLineType = CommandLineExecutor.CommandLineType.WINDOWS_CMD;
        }
        else {
            //"sh" should be supported on most Unix-like systems...
            commandLineType = CommandLineExecutor.CommandLineType.UNIX_SH;
        }

        //The "whoami" command should be supported on Windows and Unix-like systems.
        //The command returns text, so we use a "TextProcessHandler".
        CommandLineExecutor.executeCommand(commandLineType, "whoami", new TextProcessHandler() {
            @Override
            public void onInitialized(TextProcessCallback textProcessCallback) {
                //Ignore, because we don't need to send data or kill the process...
            }

            @Override
            public void onStdLineRead(TextProcessCallback textProcessCallback, String readLine) {
                //The username is written to the standard output-stream.
                usernameCallback.onUsernameRead(readLine);
            }

            @Override
            public void onErrorLineRead(TextProcessCallback textProcessCallback, String readLine) {
                //Ignore because we just read the status code to detect errors...
            }

            @Override
            public void onProcessExited(int exitCode) {
                //Notify the "user" about unexpected exit codes.
                if (exitCode != 0) {
                    usernameCallback.onException(
                            new Exception("Expected exit code 0, but read " + exitCode + "."));
                }
            }

            @Override
            public void onIOException(IOException ioException) {
                //Notify the "user" about all occurred execptions.
                usernameCallback.onException(ioException);
            }
        });
    }
}