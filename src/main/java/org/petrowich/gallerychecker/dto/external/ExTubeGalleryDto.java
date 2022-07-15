package org.petrowich.gallerychecker.dto.external;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
@ToString
public class ExTubeGalleryDto extends AbstractDto {
    private String id;
    private String url;
    private String thumbUrl;
    private String embedCode;
    private String description;
    private String duration;
    private String categories;
    private String tags;
    private String model;
    private String niche;
    private String deletedVideoUrl;
    private String quality;
    private String date;
}
