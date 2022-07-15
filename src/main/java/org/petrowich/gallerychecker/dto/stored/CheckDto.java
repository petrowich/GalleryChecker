package org.petrowich.gallerychecker.dto.stored;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
public class CheckDto extends AbstractDto {
    private Integer id;
    private String username;
    private String siteHost;
    private String checkDateTime;
    private Integer checkedGalleriesNumber;
    private String status;
    private String errorMessage;
}
