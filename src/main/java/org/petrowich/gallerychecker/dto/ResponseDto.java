package org.petrowich.gallerychecker.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.dto.enums.ToastType;

import java.io.Serializable;

@Setter
@Getter
@Accessors(chain = true)
public class ResponseDto extends AbstractDto {
    private ToastType toastType;
    private String title;
    private String message;
    private Serializable payload;
}
