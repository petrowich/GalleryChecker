package org.petrowich.gallerychecker.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.delete;

@Log4j2
@Component
public class FileHelper {

    private final FileDownloader fileDownloader;

    public FileHelper(FileDownloader fileDownloader) {
        this.fileDownloader = fileDownloader;
    }

    public List<File> downloadZip(File destinationDirectory, URI uri) throws IOException {
        return fileDownloader.downloadZip(destinationDirectory, uri);
    }

    public File downloadText(File destinationDirectory, URI uri) throws IOException {
        return fileDownloader.downloadText(destinationDirectory, uri);
    }

    public File createTempDirectory(String targetDirectory) throws IOException {
        String createSubdirectoryName = String.valueOf(UUID.randomUUID());
        Path path = Path.of(targetDirectory, createSubdirectoryName);
        if (!Files.exists(path)) {
            createDirectories(path);
        }
        return path.toFile();
    }

    public void deleteDirectory(File directoryToBeDeleted) throws IOException {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        delete(directoryToBeDeleted.toPath());
    }
}
