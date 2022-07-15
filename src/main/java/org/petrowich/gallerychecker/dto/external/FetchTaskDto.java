package org.petrowich.gallerychecker.dto.external;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
public class FetchTaskDto extends AbstractDto {
    private String id;
    private String username;
    private Integer tubeId;
    private String host;
    private String aim;
    private boolean once;
    private String last;
    private String next;
    private Integer periodHours;
    private String status;
}
