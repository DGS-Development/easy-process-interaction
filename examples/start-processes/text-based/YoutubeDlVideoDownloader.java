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
 * The following example shows how to write a simple video-downloader by using youtube-dl.
 * You can download youtube-dl for all common operating systems.
 */
public class YoutubeDlVideoDownloader {
    public static void main(String[] args) {
        DownloaderCallback downloaderCallback = new DownloaderCallback() {
            @Override
            public void onDownloadCompleted(File downloadedFile) {
                System.out.println("Downloaded: " + downloadedFile.getAbsolutePath());
            }

            @Override
            public void onProgressChanged(double progress) {
                System.out.println("Progress: " + progress + "%");
            }

            @Override
            public void onEstimatedTimeUpdate(String timeString) {
                System.out.println("ETA: " + timeString);
            }

            @Override
            public void onException(Exception exception) {
                System.err.println("Unable to download video:");
                exception.printStackTrace();
            }
        };

        //We try to download a Creative Commons video.
        YoutubeDlVideoDownloader.downloadVideo(
                "youtube-dl.exe", //Enter the path to the correct executable for your OS.
                "https://www.youtube.com/watch?v=aqz-KE-bpKQ",
                DownloadOption.WORST,
                new File("downloadedVideos"), //Download directory.
                downloaderCallback);
    }

    public enum DownloadOption {
        BEST("best"),
        WORST("worst");

        private final String option;

        DownloadOption(String option) {
            this.option = option;
        }

        public String getOption() {
            return option;
        }
    }

    public interface DownloaderCallback {
        void onDownloadCompleted(File downloadedFile);

        void onProgressChanged(double progress);

        void onEstimatedTimeUpdate(String timeString);

        void onException(Exception exception);
    }

    public static void downloadVideo(String youtubeDlPath, String videoUrl, DownloadOption downloadOption,
                                     File downloadDirectory, DownloaderCallback downloaderCallback) {
        File youtubeDlFile = new File(youtubeDlPath);

        if (!downloadDirectory.exists() && !downloadDirectory.mkdirs()) {
            downloaderCallback.onException(new Exception("Unable to create download directory: " +
                    downloadDirectory.getAbsolutePath()));
        } else if (!downloadDirectory.isDirectory()) {
            downloaderCallback.onException(new Exception("The given directory is invalid: " +
                    downloadDirectory.getAbsolutePath()));
        } else {
            List<String> arguments = new LinkedList<>();
            arguments.add("-f");
            arguments.add(downloadOption.getOption());
            arguments.add(videoUrl);

            TextProcessHandler textProcessHandler = new TextProcessHandler() {
                private static final String DESTINATION_STRING = "Destination: ";
                private static final String ETA_STRING = "ETA ";

                private String filename;

                @Override
                public void onInitialized(TextProcessCallback textProcessCallback) {
                    //Ignore, because we don't need to send data or kill the process...
                }

                @Override
                public void onStdLineRead(TextProcessCallback textProcessCallback, String readLine) {
                    //The status is written to the standard output stream, try to extract data.
                    if (readLine.contains(DESTINATION_STRING)) {
                        //Example (states the name of the downloaded file):
                        //[download] Destination: Big Buck Bunny 60fps 4K - Official Blender Foundation Short
                        //Film-aqz-KE-bpKQ.m4a

                        filename = readLine.substring(readLine.indexOf(DESTINATION_STRING) +
                                DESTINATION_STRING.length()).trim();
                    } else if (readLine.contains("%")) {
                        //Example: [download]   0.0% of 29.34MiB at  5.37KiB/s ETA 01:33:13

                        String percentageString = readLine.substring(readLine.indexOf("]") + 1,
                                readLine.indexOf("%")).trim();

                        downloaderCallback.onProgressChanged(Double.parseDouble(percentageString));

                        if (readLine.contains(ETA_STRING)) {
                            String timeString = readLine.substring(readLine.indexOf(ETA_STRING) + ETA_STRING.length());
                            downloaderCallback.onEstimatedTimeUpdate(timeString);
                        }
                    }
                }

                @Override
                public void onErrorLineRead(TextProcessCallback textProcessCallback, String readLine) {
                    //Ignore, because we read the exit code in this example...
                }

                @Override
                public void onProcessExited(int exitCode) {
                    //Notify the "user" about unexpected exit codes.
                    if (exitCode != 0) {
                        downloaderCallback.onException(new Exception("Download failed. Expected exit code 0 but read " +
                                exitCode + "."));
                    } else {
                        downloaderCallback.onDownloadCompleted(new File(downloadDirectory.getAbsolutePath() + "/" +
                                filename));
                    }
                }

                @Override
                public void onIOException(IOException ioException) {
                    //Notify the "user" about all occurred exceptions.
                    downloaderCallback.onException(ioException);
                }
            };

            ProcessCreator.startProcess(
                    youtubeDlFile,
                    downloadDirectory, //Use the download directory as working directory.
                    arguments,
                    textProcessHandler);
        }
    }
}