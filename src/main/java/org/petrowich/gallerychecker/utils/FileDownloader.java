package org.petrowich.gallerychecker.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Log4j2
@Component
public class FileDownloader {

    public List<File> downloadZip(File destinationDirectory, URI uri) throws IOException {
        File zipFile = createFile(uri, destinationDirectory);
        download(uri, zipFile);
        List<File> files = unzip(destinationDirectory, zipFile);
        if (files.isEmpty() && destinationDirectory.exists()) {
            Files.delete(destinationDirectory.toPath());
        }
        return files;
    }

    public File downloadText(File destinationDirectory, URI uri) throws IOException {
        File textFile = createFile(uri, destinationDirectory);
        download(uri, textFile);
        return textFile;
    }

    private File createFile(URI uri, File destinationDirectory) {
        Path fileName = Path.of(uri.getPath()).getFileName();
        return new File(destinationDirectory, fileName.toString());
    }

    private void download(URI uri, File file) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(uri.toURL().openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    private List<File> unzip(File destinationDirectory, File zipFile) throws IOException {
        List<File> files = new ArrayList<>();
        byte[] buffer = new byte[1024];
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                File csvFile = createCsvFile(destinationDirectory, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!csvFile.isDirectory() && !csvFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + csvFile);
                    }
                } else {
                    File parent = csvFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    try (FileOutputStream fileOutputStream = new FileOutputStream(csvFile)) {
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, len);
                        }
                    }
                }
                files.add(csvFile);
                zipEntry = zipInputStream.getNextEntry();
            }
        }
        return files;
    }

    private static File createCsvFile(File destinationDirectory, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDirectory, zipEntry.getName());

        String destDirPath = destinationDirectory.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}
