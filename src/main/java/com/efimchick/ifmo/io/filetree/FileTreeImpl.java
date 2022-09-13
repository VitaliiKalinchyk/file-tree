package com.efimchick.ifmo.io.filetree;

import java.io.File;
import java.nio.file.*;
import java.util.*;

public class FileTreeImpl implements FileTree {
    private final StringBuilder stringBuilder = new StringBuilder();

    @Override
    public Optional<String> tree(Path path) {
        if (path == null || Files.notExists(path)) {
            return Optional.empty();
        }
        if (Files.isRegularFile(path)) {
            return Optional.of(fileToString(path.toFile()));
        }
        directoryToString(path.toFile(), new ArrayList<>());
        return Optional.of(stringBuilder.toString());
    }

    private void directoryToString(File file, ArrayList<Boolean> arrayList) {
        if (!arrayList.isEmpty())
            stringBuilder.append(arrayList.get(arrayList.size() - 1) ? "└─ " : "├─ ");
        stringBuilder.append(fileToString(file));
        File[] list = file.listFiles();
        if (list != null) {
            Arrays.sort(list, ((Comparator<File>)
                    (o1, o2) -> o1.isFile() ? (o2.isFile() ? 0 : 1) : (o2.isFile() ? -1 : 0))
                    .thenComparing((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName())));
            for (int i = 0; i < list.length; i++) {
                stringBuilder.append("\n");
                for (Boolean aBoolean : arrayList) {
                    stringBuilder.append(aBoolean ? "   " : "│  ");
                }
                if (list[i].isDirectory()) {
                    ArrayList<Boolean> newArrayList = new ArrayList<>(arrayList);
                    newArrayList.add(i+1 == list.length);
                    directoryToString(list[i], newArrayList);
                }
                else {
                    stringBuilder.append(i != list.length - 1 ? "├─ " : "└─ ").append(fileToString(list[i]));
                }
            }
        }
    }

    private String fileToString(File file) {
        return file.getName() + " " + (file.isFile() ? file.length() : directorySize(file)) + " bytes";
    }

    private long directorySize(File file) {
        long size = 0;
        File[] list = file.listFiles();
        if (list != null) {
            for (File file1 : list) {
                if (file1.isFile()) {
                    size += file1.length();
                } else {
                    size += directorySize(file1);
                }
            }
        }
        return size;
    }
}