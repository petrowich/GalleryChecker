package org.petrowich.gallerychecker.integration.tubes.xvideos;

import org.junit.jupiter.api.Test;
import org.petrowich.gallerychecker.processig.external.xvideos.readers.XVideosNewReader;

import java.io.File;

class XVideosNewReaderTest {

    private final static String pathFile = "src/test/java/resources/XVideos/downloads/xvideos.com-export-week.csv";
    private XVideosNewReader xVideosNewReader = new XVideosNewReader();




    @Test
    void getRecords() {
        xVideosNewReader.readCsv(new File(pathFile));
    }
}