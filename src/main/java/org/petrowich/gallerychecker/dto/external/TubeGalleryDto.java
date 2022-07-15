package org.petrowich.gallerychecker.dto.external;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
public class TubeGalleryDto extends AbstractDto {
    private Integer id;
    private Integer fetchId;
    private Integer tubeId;
    private String externalId;
    private String url;
    private String thumbUrl;
    private String embedCode;
    private String videoUrl;
    private Integer duration;
    private String description;
    private String tags;
    private String model;
    private String date;
    private boolean active;
}
