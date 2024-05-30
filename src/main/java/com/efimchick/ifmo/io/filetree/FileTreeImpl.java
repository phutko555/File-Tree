package com.efimchick.ifmo.io.filetree;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class FileTreeImpl implements FileTree {

    @Override
    public Optional<String> tree(Path path) {
        if (path == null || !Files.exists(path))
            return Optional.empty();

        try {
            StringBuilder treeBuilder = new StringBuilder();
            walkTree(path, "", "", treeBuilder);
            return Optional.of(treeBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void walkTree(Path path, String prefix, String prefixForFiles, StringBuilder treeBuilder) throws IOException {
        boolean isDirectory = Files.isDirectory(path);
        String name = path.getFileName().toString();
        treeBuilder.append(prefix).append(isDirectory ? "├─ " : prefixForFiles.isEmpty() ? "└─ " : "│  ").append(name);

        if (isDirectory) {
            treeBuilder.append("\n");
            List<Path> contents = Files.list(path)
                    .sorted((p1, p2) -> {
                        boolean p1IsDir = Files.isDirectory(p1);
                        boolean p2IsDir = Files.isDirectory(p2);
                        if (p1IsDir && !p2IsDir) return -1;
                        if (!p1IsDir && p2IsDir) return 1;
                        return p1.getFileName().toString().compareToIgnoreCase(p2.getFileName().toString());
                    })
                    .collect(Collectors.toList());

            for (int i = 0; i < contents.size(); i++) {
                Path contentPath = contents.get(i);
                String subPrefix = prefix + (i == contents.size() - 1 ? "   " : "│  ");
                String subPrefixForFiles = prefix + (i == contents.size() - 1 ? "└─ " : "├─ ");
                walkTree(contentPath, subPrefix, subPrefixForFiles, treeBuilder);
            }
        } else {
            long fileSize = Files.size(path);
            treeBuilder.append(" ").append(formatSize(fileSize)).append("\n");
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

