package org.petrowich.gallerychecker.integration.services;

import org.junit.jupiter.api.Test;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.services.master.TubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;

@SpringBootTest
class TubeServiceTest {

    @Autowired
    private TubeService tubeService;

    @Test
    void findByHost() {
        tubeService.findByHost("xvideos.com");
    }

    @Test
    void findAll() {
        Collection<Tube> tubes = tubeService.findAll();
        System.out.println(tubes);
    }
}