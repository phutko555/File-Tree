package com.efimchick.ifmo.io.filetree;
import java.nio.file.*;
import java.util.Optional;
public class FileTreeImpl implements FileTree {
    @Override
    public Optional<String> tree(Path path) {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return Optional.empty();
        }

        StringBuilder result = new StringBuilder();
        try {
            buildTree(path, result, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(result.toString());
    }
    private void buildTree(Path path, StringBuilder result, int level) throws Exception {
        result.append(indent(level)).append(path.getFileName()).append("\n");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
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
