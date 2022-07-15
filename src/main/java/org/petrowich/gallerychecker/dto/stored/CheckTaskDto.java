package org.petrowich.gallerychecker.dto.stored;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

@Setter
@Getter
@Accessors(chain = true)
public class CheckTaskDto extends AbstractDto {
    private String id;
    private String username;
    private Integer siteId;
    private String host;
    private String aim;
    private boolean once;
    private String last;
    private String next;
    private Integer periodDays;
    private String status;
}
