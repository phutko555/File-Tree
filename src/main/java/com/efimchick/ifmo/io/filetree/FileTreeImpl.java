package com.efimchick.ifmo.io.filetree;


import java.io.File;
import java.nio.file.Path;
import java.util.*;



public class FileTreeImpl implements FileTree {

    @Override
    public Optional<String> tree(Path path) {
        if (path == null) {
            return Optional.empty();
        }

        File file = path.toFile();

        if (file.isFile()) {
            return Optional.of(file.getName() + " " + file.length() + " bytes");
        }

        if (file.isDirectory()) {
            StringBuilder sb = new StringBuilder();
            formatTree(file, sb, "", "");
            return Optional.of(sb.toString());
        }
        return Optional.empty();
    }

    private void formatTree(File file, StringBuilder sb, String indent, String prefix) {
        sb.append(prefix).append(file.getName()).append(" ");
        if(file.isDirectory()){
            sb.append(getDirectorySize(file)).append(" bytes").append("\n");
        } else if (file.isFile()) {
            sb.append(file.length()).append(" bytes").append("\n");
        }

        if (file.isDirectory()) {
            File[] sortedFiles = file.listFiles();
            if (sortedFiles != null) {
                Arrays.sort(sortedFiles, (f1, f2) -> {
                    if (f1.isDirectory() && !f2.isDirectory()) {
                        return -1;
                    } else if (!f1.isDirectory() && f2.isDirectory()) {
                        return 1;
                    } else {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                });

                for (int i = 0; i < sortedFiles.length; i++) {
                    File currentFile = sortedFiles[i];
                    boolean isLast = (i == sortedFiles.length - 1);

                    if (isLast) {
                        formatTree(currentFile, sb, indent + "   ", indent + "└─ ");
                    } else {
                        formatTree(currentFile, sb, indent + "│  ", indent + "├─ ");
                    }
                }
            }
        }
    }

    public static Long getDirectorySize(File dir) {
        Long size = 0L;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += getDirectorySize(file);
                }
            }
        }
        return size;
    }
}

