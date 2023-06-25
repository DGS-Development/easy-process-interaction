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

package main

import (
	"bufio"
	"bytes"
	"fmt"
	"os"
)

func main() {
	userArguments := os.Args[1:]

	if len(userArguments) == 0 {
		os.Exit(0)
	}

	if userArguments[0] == "echo-string" {
		scanner := bufio.NewScanner(os.Stdin)

		for scanner.Scan() {
			readLine := scanner.Text()

			if readLine == "EXIT" {
				os.Exit(0)
			}

			bytesWritten, writeError := fmt.Println(readLine)

			if bytesWritten < len(readLine) {
				os.Exit(2)
			} else if writeError != nil {
				os.Exit(3)
			}
		}
	} else if userArguments[0] == "echo-bytes" {
		stopBytes := []byte{69, 88, 73, 84}

		bufferBytes := []byte{0, 0, 0, 0}

		for true {
			readBytesCount, readError := os.Stdin.Read(bufferBytes)

			if readBytesCount < len(bufferBytes) {
				os.Exit(4)
			} else if readError != nil {
				os.Exit(5)
			}

			if bytes.Equal(bufferBytes, stopBytes) {
				os.Exit(0)
			}

			bytesWritten, writeError := os.Stdout.Write(bufferBytes)

			if bytesWritten < len(bufferBytes) {
				os.Exit(6)
			} else if writeError != nil {
				os.Exit(7)
			}
		}
	} else if userArguments[0] == "echo-bytes-stderr" {
		stopBytes := []byte{69, 88, 73, 84}

		bufferBytes := []byte{0, 0, 0, 0}

		for true {
			readBytesCount, readError := os.Stdin.Read(bufferBytes)

			if readBytesCount < len(bufferBytes) {
				os.Exit(4)
			} else if readError != nil {
				os.Exit(5)
			}

			if bytes.Equal(bufferBytes, stopBytes) {
				os.Exit(0)
			}

			bytesWritten, writeError := os.Stderr.Write(bufferBytes)

			if bytesWritten < len(bufferBytes) {
				os.Exit(6)
			} else if writeError != nil {
				os.Exit(7)
			}
		}
	} else if userArguments[0] == "pwd" {
		directoryPath, directoryError := os.Getwd()

		if directoryError != nil {
			os.Exit(10)
		}

		fmt.Println(directoryPath)

		os.Exit(0)
	}
}
