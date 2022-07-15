package org.petrowich.gallerychecker.integration.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.petrowich.gallerychecker.utils.FileDownloader;

import java.io.File;
import java.io.IOException;
import java.net.URI;

class FileDownloaderTest {
    private static final URI weeklyNewUri = URI.create("https://webmaster-tools.xvideos.com/xvideos.com-export-week.csv.zip");
    private AutoCloseable autoCloseable;

    @InjectMocks
    private FileDownloader fileDownloader;

    @BeforeEach
    private void openMocks() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void downloadZipGalleries() throws IOException {
        fileDownloader.downloadZip(new File("temp"), weeklyNewUri);
    }
}