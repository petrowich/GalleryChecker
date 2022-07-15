package org.petrowich.gallerychecker.integration.processing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.petrowich.gallerychecker.processig.stored.StoredGalleryHelper;
import org.springframework.test.context.ActiveProfiles;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@ActiveProfiles("test")
class StoredStoredGalleryHelperTest {

    private final static String EMBED_CODE = "<iframe width=\"960\" height=\"540\"" +
            " src=\"https://www.xozilla.com/embed/85772\" frameborder=\"0\"" +
            " allowfullscreen webkitallowfullscreen mozallowfullscreen oallowfullscreen msallowfullscreen></iframe>";

    private AutoCloseable autoCloseable;

    @InjectMocks
    private StoredGalleryHelper storedGalleryHelper;

    @BeforeEach
    private void openMocks() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void extractVideoLincFromEmbedCode() throws ParserConfigurationException, IOException, SAXException {
        StoredGallery storedGallery = new StoredGallery().setEmbedCode(EMBED_CODE);
        //storedGalleryHelper.extractEmbedUrlFromEmbedCode(storedGallery);
        System.out.println(storedGallery.getEmbedUrl());
    }
}