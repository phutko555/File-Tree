package com.efimchick.ifmo.io.filetree;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileTreeImpl implements FileTree {

    @Override
    public Optional<String> tree(Path path) {
        if (path == null || !Files.exists(path))
            return Optional.empty();

        try {
            return Optional.of(generateTree(path, "", true));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String generateTree(Path path, String prefix, boolean isLast) throws IOException {
        StringBuilder treeBuilder = new StringBuilder();
        String name = path.getFileName().toString();
        String formattedName = isLast ? "└─ " + name : "├─ " + name;

        if (Files.isDirectory(path)) {
            List<Path> contents = getSortedContents(path);
            long totalSize = 0;

            for (int i = 0; i < contents.size(); i++) {
                Path contentPath = contents.get(i);
                boolean isLastContent = (i == contents.size() - 1);
                String subPrefix = prefix + (isLast ? "    " : "│   ");
                String contentTree = generateTree(contentPath, subPrefix, isLastContent);
                treeBuilder.append(contentTree);
                totalSize += Files.isDirectory(contentPath) ? getDirectorySize(contentPath) : Files.size(contentPath);
            }

            String formattedSize = formatSize(totalSize);
            treeBuilder.insert(0, prefix + formattedName + " " + formattedSize + "\n");
        } else {
            long fileSize = Files.size(path);
            String formattedSize = formatSize(fileSize);
            treeBuilder.append(prefix).append(formattedName).append(" ").append(formattedSize).append("\n");
        }

        return treeBuilder.toString();
    }

    private List<Path> getSortedContents(Path directory) throws IOException {
        try (Stream<Path> list = Files.list(directory)) {
            return list.sorted((p1, p2) -> {
                boolean p1IsDir = Files.isDirectory(p1);
                boolean p2IsDir = Files.isDirectory(p2);
                if (p1IsDir && !p2IsDir) return -1;
                if (!p1IsDir && p2IsDir) return 1;
                return p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString());
            }).collect(Collectors.toList());
        }
    }

    private long getDirectorySize(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }).sum();
        }
    }

    private String formatSize(long size) {
        if (size < 1024) {
            return size + " bytes";
        } else if (size < 1024 * 1024) {
            return (size / 1024) + " KB";
        } else {
            return (size / (1024 * 1024)) + " MB";
        }
    }
}
