package io.all.andp;

import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class AnalysisFileVisitor extends SimpleFileVisitor<Path> {
    private final CommandLine cmd;
    private final Set<String> uniqueExtensions = new HashSet<>();
    private final Set<String> allExtensions = new HashSet<>();
    private int totalFiles = 0;
    private int totalDirs = 0;
    private long totalAInHtmlFiles = 0;

    public AnalysisFileVisitor(CommandLine cmd) {
        this.cmd = cmd;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        totalFiles++;

        if (file.toString().toLowerCase().endsWith(".html")) {
            String content = new String(Files.readAllBytes(file));
            long countA = content.chars().filter(ch -> ch == 'a').count();
            totalAInHtmlFiles += countA;
        }

        String fileExtension = getFileExtension(file);
        if (fileExtension != null) {
            allExtensions.add(fileExtension);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        totalDirs++;
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.err.println("Error visiting file: " + file.toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }

    public void printResults() {
        if (cmd.hasOption("a")) {
            System.out.println("Total number of 'a' in HTML files: " + totalAInHtmlFiles);
        }

        if (cmd.hasOption("b")) {
            System.out.println("Total number of files: " + totalFiles);
            System.out.println("Total number of directories: " + totalDirs);
        }

        if (cmd.hasOption("c")) {
            System.out.println("Total number of unique file extensions: " + allExtensions.size());
        }

        if (cmd.hasOption("d")) {
            System.out.println("List of unique file extensions: " + String.join(", ", allExtensions));
        }

        if (cmd.hasOption("num-ext")) {
            String specifiedExt = cmd.getOptionValue("num-ext");
            long filesWithSpecifiedExt = allExtensions.stream().filter(ext -> ext.equals(specifiedExt)).count();
            System.out.println("Total number of files with extension " + specifiedExt + ": " + filesWithSpecifiedExt);
        }
    }

    private String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex == -1) ? null : fileName.substring(dotIndex + 1);
    }
}
