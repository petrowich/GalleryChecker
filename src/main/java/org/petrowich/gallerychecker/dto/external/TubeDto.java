package org.petrowich.gallerychecker.dto.external;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
public class TubeDto extends AbstractDto {
    private Integer id;
    private String host;
    private Integer activeTubeGalleriesNumber;
    private Integer inactiveTubeGalleriesNumber;
    private Integer storedGalleriesNumber;
    private Integer uncheckedStoredGalleriesNumber;
    private Integer unavailableStoredGalleriesNumber;
    private boolean fetchJobExists;
}
