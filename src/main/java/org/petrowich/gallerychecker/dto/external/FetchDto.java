package org.petrowich.gallerychecker.dto.external;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
public class FetchDto extends AbstractDto {
    private Integer id;
    private String username;
    private String tubeHost;
    private String fetchDateTime;
    private Integer invocationsNumber;
    private Integer fetchedGalleriesNumber;
    private String aim;
    private String status;
    private String errorMessage;
}
