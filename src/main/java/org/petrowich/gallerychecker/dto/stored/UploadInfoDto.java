package org.petrowich.gallerychecker.dto.stored;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
public class UploadInfoDto extends AbstractDto {
    private String username;
    private String siteHost;
    private String fileName;
    private String uploadDateTime;
    private Integer uploadedGalleriesNumber;
    private Integer newUploadedGalleriesNumber;
    private String status;
    private String errorMessage;
}
