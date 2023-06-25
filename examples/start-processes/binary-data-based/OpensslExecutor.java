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
 * The following example shows how to read random bytes from OpenSSL.
 * You can download OpenSSL builds for all common operating systems.
 */
public class OpensslExecutor {
    public static void main(String[] args) {
        //Enter the path to the correct executable for your OS.
        OpensslExecutor.generateRandomBytes("openssl.exe", 16, new RandomBytesCallback() {
            @Override
            public void onRandomBytesRead(byte[] randomBytes) {
                System.out.println("Random bytes:");
                System.out.println(Arrays.toString(randomBytes));
            }

            @Override
            public void onException(Exception exception) {
                System.err.println("Unable to read random bytes:");
                exception.printStackTrace();
            }
        });
    }

    public interface RandomBytesCallback {
        void onRandomBytesRead(byte[] randomBytes);

        void onException(Exception exception);
    }

    public static void generateRandomBytes(String openSslPath, int bytesToGenerate,
                                           RandomBytesCallback randomBytesCallback) {
        //See: https://www.openssl.org/docs/man1.1.1/man1/rand.html
        List<String> arguments = new LinkedList<>();
        arguments.add("rand");
        arguments.add(bytesToGenerate + "");

        File openSsl = new File(openSslPath);

        ProcessCreator.startProcessInProcessDirectory(
                openSsl,
                arguments,
                new BinaryProcessHandler() {
                    private ByteBuffer byteBuffer;

                    @Override
                    public void onInitialized(BinaryProcessCallback binaryProcessCallback) {
                        //Ignore the callback, because we don't need to send data or kill the process...
                        byteBuffer = ByteBuffer.allocate(bytesToGenerate);
                    }

                    @Override
                    public void onStdBytesRead(BinaryProcessCallback binaryProcessCallback, int readBytes, byte[] byteArray) {
                        //Try to read bytes from the standard stream.
                        byteBuffer.put(byteArray, 0, readBytes);
                    }

                    @Override
                    public void onErrorBytesRead(BinaryProcessCallback binaryProcessCallback, int readBytes, byte[] byteArray) {
                        //Ignore, because we read the exit code...
                    }

                    @Override
                    public void onProcessExited(int exitCode) {
                        //Notify the "user" about unexpected exit codes.
                        if (exitCode != 0) {
                            randomBytesCallback.onException(new Exception("Unable to read bytes: Expected exit " +
                                    "code 0 but read " + exitCode + "."));
                        } else {
                            randomBytesCallback.onRandomBytesRead(byteBuffer.array());
                        }
                    }

                    @Override
                    public void onIOException(IOException ioException) {
                        //Notify the "user" about all occurred exceptions.
                        randomBytesCallback.onException(ioException);
                    }
                });
    }
}