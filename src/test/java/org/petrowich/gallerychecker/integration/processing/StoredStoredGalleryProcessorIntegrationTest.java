package org.petrowich.gallerychecker.integration.processing;

import org.junit.jupiter.api.Test;
import org.petrowich.gallerychecker.models.checks.Check;
import org.petrowich.gallerychecker.processig.stored.StoredGalleriesCheckProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StoredStoredGalleryProcessorIntegrationTest {

    @Autowired
    private StoredGalleriesCheckProcessor storedGalleriesCheckProcessor;

    @Test
    void processGallery() {
        Check check = new Check();
        storedGalleriesCheckProcessor.checkGalleries(check);
    }
}