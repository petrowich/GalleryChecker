package org.petrowich.gallerychecker.dto.external;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
public class FetchInvocationDto extends AbstractDto {
    private Integer invocationNumber;
    private String fetchUrl;
    private Integer fetchedGalleries;
    private String status;
    private String errorMessage;
}
