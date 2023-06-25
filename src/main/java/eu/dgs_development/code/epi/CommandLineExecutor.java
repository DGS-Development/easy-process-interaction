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

import eu.dgs_development.code.epi.handlers.binary.BinaryProcessHandler;
import eu.dgs_development.code.epi.handlers.text.TextProcessHandler;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to execute commands from a native command line process.
 */
public final class CommandLineExecutor {
    private static final String WINDOWS_DIRECTORY = System.getenv("WINDIR");

    /**
     * An enum class representing all available command line types to start a process from.
     */
    public enum CommandLineType {
        /**
         * Runs a command through a 64-Bit Powershell process instance.
         */
        WINDOWS_POWERSHELL_64BIT(new File(WINDOWS_DIRECTORY + "/System32/WindowsPowerShell/v1.0/" +
                "powershell.exe"), "-Command"),
        /**
         * Runs a command through a 32-Bit Powershell process instance.
         */
        WINDOWS_POWERSHELL_32BIT(new File(WINDOWS_DIRECTORY + "/SysWOW64/WindowsPowerShell/v1.0/" +
                "powershell.exe"), "-Command"),
        /**
         * Runs a command through a "cmd.exe" process instance.
         */
        WINDOWS_CMD(new File(WINDOWS_DIRECTORY + "/system32/cmd.exe"), "/C"),
        /**
         * Runs a command through a "/bin/sh" process instance. Please note that you CAN'T MIX single quotes with double
         * quotes in the provided command arguments.
         */
        UNIX_SH(new File("/bin/sh"), "-c"),
        /**
         * Runs a command through a "/bin/bash" process instance. Please note that you CAN'T MIX single quotes with
         * double quotes in the provided command arguments.
         */
        UNIX_BASH(new File("/bin/bash"), "-c");

        private final File processFile;
        private final String commandArgument;

        CommandLineType(File processFile, String commandArgument) {
            this.processFile = processFile;
            this.commandArgument = commandArgument;
        }

        /**
         * Returns the executable file of the command line.
         * @return The command line executable path.
         */
        public File getProcessFile() {
            return processFile;
        }

        /**
         * Returns the command line argument used to pass a command to the executable.
         * @return The command line argument to pass a command to the executable.
         */
        public String getCommandArgument() {
            return commandArgument;
        }
    }

    private CommandLineExecutor() {
        //Ignore...
    }

    /**
     * Starts a command line and passes a list of arguments to it, using the program directory as working directory.
     * @param commandLineType The command line type.
     * @param arguments The arguments to start the command line with.
     * @param textProcessHandler The handler to handle the command line interactions.
     */
    public static void startWithArguments(CommandLineType commandLineType, List<String> arguments,
                                          TextProcessHandler textProcessHandler) {
        startWithArguments(commandLineType, null, arguments, textProcessHandler);
    }

    /**
     * Starts a command line and passes a list of arguments to it, using the program directory as working directory.
     * @param commandLineType The command line type.
     * @param arguments The arguments to start the command line with.
     * @param binaryProcessHandler The handler to handle the command line interactions.
     */
    public static void startWithArguments(CommandLineType commandLineType, List<String> arguments,
                                          BinaryProcessHandler binaryProcessHandler) {
        startWithArguments(commandLineType, null, arguments, binaryProcessHandler);
    }

    /**
     * Starts a command line and passes a list of arguments to it.
     * @param commandLineType The command line type.
     * @param workingDirectory The working directory for the command line.
     * @param arguments The arguments to start the command line with.
     * @param textProcessHandler The handler to handle the command line interactions.
     */
    public static void startWithArguments(CommandLineType commandLineType, File workingDirectory,
                                          List<String> arguments, TextProcessHandler textProcessHandler) {
        ProcessCreator.startProcess(commandLineType.getProcessFile(), workingDirectory,
                arguments, textProcessHandler);
    }

    /**
     * Starts a command line and passes a list of arguments to it.
     * @param commandLineType The command line type.
     * @param workingDirectory The working directory for the command line.
     * @param arguments The arguments to start the command line with.
     * @param binaryProcessHandler The handler to handle the command line interactions.
     */
    public static void startWithArguments(CommandLineType commandLineType, File workingDirectory,
                                          List<String> arguments, BinaryProcessHandler binaryProcessHandler) {
        ProcessCreator.startProcess(commandLineType.getProcessFile(), workingDirectory,
                arguments, binaryProcessHandler);
    }

    /**
     * Executes a command for a given {@see CommandLineType}, using the program directory as working directory.
     * @param commandLineType The command line type.
     * @param command The command to execute.
     * @param textProcessHandler The handler to handle the command line interactions.
     */
    public static void executeCommand(CommandLineType commandLineType, String  command,
                                      TextProcessHandler textProcessHandler) {
        executeCommand(commandLineType, null, command, null, textProcessHandler);
    }

    /**
     * Executes a command for a given {@see CommandLineType}, using the program directory as working directory.
     * @param commandLineType The command line type.
     * @param command The command to execute.
     * @param binaryProcessHandler The handler to handle the command line interactions.
     */
    public static void executeCommand(CommandLineType commandLineType, String  command,
                                      BinaryProcessHandler binaryProcessHandler) {
        executeCommand(commandLineType, null, command, null, binaryProcessHandler);
    }

    /**
     * Executes a command for a given {@see CommandLineType}, using the program directory as working directory.
     * @param commandLineType The command line type.
     * @param command The command to execute.
     * @param arguments The arguments for the command.
     * @param textProcessHandler The handler to handle the command line interactions.
     */
    public static void executeCommand(CommandLineType commandLineType, String  command, List<String> arguments,
                                      TextProcessHandler textProcessHandler) {
        executeCommand(commandLineType, null, command, arguments, textProcessHandler);
    }

    /**
     * Executes a command for a given {@see CommandLineType}, using the program directory as working directory.
     * @param commandLineType The command line type.
     * @param command The command to execute.
     * @param arguments The arguments for the command.
     * @param binaryProcessHandler The handler to handle the command line interactions.
     */
    public static void executeCommand(CommandLineType commandLineType, String  command, List<String> arguments,
                                      BinaryProcessHandler binaryProcessHandler) {
        executeCommand(commandLineType, null, command, arguments, binaryProcessHandler);
    }

    /**
     * Executes a command for a given {@see CommandLineType}.
     * @param commandLineType The command line type.
     * @param workingDirectory The working directory for the command line.
     * @param command The command to execute.
     * @param arguments The arguments for the command.
     * @param textProcessHandler The handler to handle the command line interactions.
     */
    public static void executeCommand(CommandLineType commandLineType, File workingDirectory, String  command,
                                      List<String> arguments, TextProcessHandler textProcessHandler) {
        ProcessCreator.startProcess(
                commandLineType.getProcessFile(),
                workingDirectory,
                createCommandArguments(
                        commandLineType,
                        command,
                        arguments),
                textProcessHandler);
    }

    /**
     * Executes a command for a given {@see CommandLineType}.
     * @param commandLineType The command line type.
     * @param workingDirectory The working directory for the command line.
     * @param command The command to execute.
     * @param arguments The arguments for the command.
     * @param binaryProcessHandler The handler to handle the command line interactions.
     */
    public static void executeCommand(CommandLineType commandLineType, File workingDirectory, String  command,
                                      List<String> arguments, BinaryProcessHandler binaryProcessHandler) {
        ProcessCreator.startProcess(
                commandLineType.getProcessFile(),
                workingDirectory,
                createCommandArguments(
                        commandLineType,
                        command,
                        arguments),
                binaryProcessHandler);
    }

    private static List<String> createCommandArguments(CommandLineType commandLineType, String command,
                                                       List<String> arguments) {
        List<String> newArguments = new LinkedList<>();

        newArguments.add(commandLineType.getCommandArgument());

        //Try to combine command and arguments to a single string, if the user uses sh or bash.
        if(commandLineType == CommandLineType.UNIX_SH || commandLineType == CommandLineType.UNIX_BASH) {
            newArguments.add(createSingleCommandString(command, arguments));
        }
        else {
            //We don't use sh or bash. No need to combine command and arguments to a single string.
            newArguments.add(command);

            if(arguments != null)
                newArguments.addAll(arguments);
        }

        return newArguments;
    }

    private static String createSingleCommandString(String command, List<String> arguments) {
        //Check if the arguments contain mixed quotes (single quotes and double quotes).
        if(arguments != null) {
            boolean containsSingleQuotes = false;
            boolean containsDoubleQuotes = false;

            for(String tmpArgument : arguments) {
                if(!containsSingleQuotes)
                    containsSingleQuotes = tmpArgument.contains("'");

                if(!containsDoubleQuotes)
                    containsDoubleQuotes = tmpArgument.contains("\"");

                //We found both quote types in some arguments. No need to search anymore.
                if(containsSingleQuotes && containsDoubleQuotes)
                    break;
            }

            if(containsSingleQuotes && containsDoubleQuotes)
                throw new RuntimeException("Illegal command arguments: It's forbidden to mix single quotes with " +
                        "double quotes in command arguments when using \"/bin/sh\" or \"/bin/bash\".");
        }

        //Create a single command string, including all arguments.
        StringBuilder singleCommandStringBuilder = new StringBuilder();
        singleCommandStringBuilder.append(command);

        if(arguments != null) {
            for(String tmpArgument : arguments) {
                singleCommandStringBuilder.append(" ").append(tmpArgument);
            }
        }

        return singleCommandStringBuilder.toString();
    }
}
