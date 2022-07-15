package org.petrowich.gallerychecker.models.master.tubes;

public interface TubeGalleriesCount {
    Tube getTube();

    Integer getActiveTubeGalleriesNumber();

    Integer getInactiveTubeGalleriesNumber();
}
