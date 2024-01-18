package io.all.andp;

import org.apache.commons.cli.*;

import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

    public class App {
        public static void main(String[] args) {
            try {
                CommandLineParser parser = new DefaultParser();
                CommandLine cmd = parser.parse(getOptions(), args);

                if (!cmd.hasOption("f") || !cmd.hasOption("a") && !cmd.hasOption("b") && !cmd.hasOption("c") && !cmd.hasOption("d") && !cmd.hasOption("num-ext")) {
                    printHelp();
                    return;
                }

                String folderPath = cmd.getOptionValue("f");

                Path folder = Paths.get(folderPath);

                if (!Files.exists(folder) || !Files.isDirectory(folder)) {
                    System.out.println("Invalid folder path. Please provide a valid directory.");
                    return;
                }

                AnalysisFileVisitor fileVisitor = new AnalysisFileVisitor(cmd);
                Files.walkFileTree(folder, fileVisitor);

                fileVisitor.printResults();

            } catch (ParseException e) {
                System.out.println("Error parsing command line arguments: " + e.getMessage());
                printHelp();
            } catch (IOException e) {
                System.out.println("Error accessing folder: " + e.getMessage());
            }
        }


        private static Options getOptions() {
            Options options = new Options();

            options.addOption("a", "total-num-files", false, "Print the total number of files");
            options.addOption("b", "total-num-dirs", false, "Print the total number of directories");
            options.addOption("c", "total-unique-exts", false, "Print the total number of unique file extensions");
            options.addOption("d", "list-exts", false, "Print the list of all unique file extensions, i.e., only print each extension exactly once");
            Option numExtOption = new Option(null, "num-ext", true, "Print the total number of files for the specified extension EXT");
            numExtOption.setArgName("EXT");
            options.addOption(numExtOption);
            Option folderOption = new Option("f", "path-to-folder", true, "Specify the path to the documentation folder (required)");
            folderOption.setRequired(true);
            folderOption.setArgName("path");
            options.addOption(folderOption);

            return options;
        }

        private static void printHelp() {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar problem2-jarfile-name.jar", getOptions());
        }
    }

