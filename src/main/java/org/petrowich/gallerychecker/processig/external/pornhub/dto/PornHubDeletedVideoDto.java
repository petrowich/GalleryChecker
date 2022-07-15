package org.petrowich.gallerychecker.processig.external.pornhub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PornHubDeletedVideoDto {
    @JsonProperty("vkey")
    private String videoKey;
    @JsonProperty("deleted_on")
    private String deletedOn;
}
