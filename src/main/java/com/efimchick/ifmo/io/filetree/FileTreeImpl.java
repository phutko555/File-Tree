package com.efimchick.ifmo.io.filetree;
import java.nio.file.*;
import java.io.IOException;

import java.util.*;
public class FileTreeImpl implements FileTree {

    @Override
    public Optional<String> tree(Path path) {
        if (path == null || !Files.exists(path)) {
            return Optional.empty();
        }

        StringBuilder result = new StringBuilder();
        try {
            buildTree(path, result, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(result.toString().trim());
    }

    private void buildTree(Path path, StringBuilder result, int level) throws IOException {
        result.append(indent(level)).append(path.getFileName()).append("\n");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            List<Path> entries = new ArrayList<>();
            for (Path entry : stream) {
                entries.add(entry);
            }
            entries.sort(Comparator.comparing(Path::getFileName, Comparator.comparing(Object::toString, String.CASE_INSENSITIVE_ORDER)));

            for (int i = 0; i < entries.size(); i++) {
                Path entry = entries.get(i);
                if (Files.isDirectory(entry)) {
                    buildTree(entry, result, level + 1);
                } else {
                    result.append(indent(level + 1)).append(entry.getFileName()).append("\n");
                }
            }
        }
    }

    private String indent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }
}
