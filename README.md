# easy-process-interaction

easy-process-interaction is a small Java library to facilitate process interactions.

This library has already been used to burn discs (cdbxpcmd.exe), start and manage message broker processes (using NATS and Python), convert video data (FFmpeg) and many other use-cases to extend the capabilities of Java software.

Use native operating system commands or run any software with command line support! Just bundle or install the native software and execute it with this library. 🚀

# Features 📦

* Supports Java 8 or newer
* Lightweight library with no additional dependencies
* Start processes and interact with them (read and write data or stop the process)
* Read and write text or binary data, by using the standard in- and output-streams of a started process
* Execute commands or start programs, by using the native command line (cmd.exe, PowerShell, sh or bash)

# Usage ⚙

The examples in the "examples"-directory show you how to use the library. 

You could either use the "CommandLineExecutor"-class or the "ProcessCreator"-class to start processes. Both classes contain static functions, which require the user to pass a "TextProcessHandler" or a "BinaryProcessHandler" in order to start a process (see the code example below). After the process started, the handler gets notified when data was read. Note that the notifications come from a **reader thread**, and not from the thread used to start the process. It is also possible to send data to the process or to stop the process by using the "TextProcessCallback" or the "BinaryProcessCallback" inside the handler-implementation.

```java
//The "whoami" command should be supported on Windows and Unix-like systems.
//The command returns text, so we use a "TextProcessHandler". Otherwise we could use a "BinaryProcessHandler".
//We assume that we use a Linux-installation and that the "sh"-terminal is available.
CommandLineExecutor.executeCommand(CommandLineExecutor.CommandLineType.UNIX_SH, "whoami", new TextProcessHandler() {
    @Override
    public void onInitialized(TextProcessCallback textProcessCallback) {
        //Ignore, because we don't need to send data or kill the process...
    }

    @Override
    public void onStdLineRead(TextProcessCallback textProcessCallback, String readLine) {
        //The username is written to the standard output-stream.
        System.out.println("Read line from \"whoami\": " + readLine);
    }

    @Override
    public void onErrorLineRead(TextProcessCallback textProcessCallback, String readLine) {
        //Ignore, because we just read the status code to detect errors...
    }

    @Override
    public void onProcessExited(int exitCode) {
        System.out.println("\"whoami\" exited with status code: " + exitCode);
    }

    @Override
    public void onIOException(IOException ioException) {
        ioException.printStackTrace();
    }
});
```

## Execute commands

If you want to execute command line commands, or if you want to start a process by using the command line, use the static functions inside the "CommandLineExecutor"-class (see the examples in the "examples/execute-commands" directory).

## Start processes

If you want to start processes use the functions inside the "ProcessCreator"-class (see the examples in the "examples/start-processes" directory).

# Installation 🔨

The easiest way to use the library in your project is to add it as a jitpack-dependency.

## Maven

Add the jitpack repository.

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency.

```xml
<dependency>
    <groupId>eu.dgs-development</groupId>
    <artifactId>easy-process-interaction</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Gradle

Add the jitpack repository.

```text
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```

Add the dependency.

```text
dependencies {
    implementation 'eu.dgs-development:easy-process-interaction:1.0.0'
}
```