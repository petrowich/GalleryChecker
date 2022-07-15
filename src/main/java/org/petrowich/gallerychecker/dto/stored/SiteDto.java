package org.petrowich.gallerychecker.dto.stored;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
public class SiteDto extends AbstractDto {
    private Integer id;
    private String name;
    private String host;
    private Integer tubesNumber;
    private Integer galleriesNumber;
    private Integer uncheckedGalleriesNumber;
    private Integer availableCheckedGalleriesNumber;
    private Integer unavailableCheckedGalleriesNumber;
}
