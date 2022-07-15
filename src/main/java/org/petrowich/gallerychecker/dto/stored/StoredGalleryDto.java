package org.petrowich.gallerychecker.dto.stored;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.AbstractDto;

import java.time.LocalDateTime;

@Setter
@Getter
@Accessors(chain = true)
public class StoredGalleryDto extends AbstractDto {
    private LocalDateTime uploadDateTime;
    private String url;
    private String thumbUrl;
    private String galleryDatetime;
    private String embedCode;
    private String videoUrl;
    private boolean checked;
    private boolean available;
    private String statusMessage;
    private boolean error;
    private String errorMessage;
    private String checkDateTime;
}
