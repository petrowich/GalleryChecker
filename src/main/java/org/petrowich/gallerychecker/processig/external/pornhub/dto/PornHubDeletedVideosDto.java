package org.petrowich.gallerychecker.processig.external.pornhub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class PornHubDeletedVideosDto {
    @JsonProperty("videos")
    private List<PornHubDeletedVideoDto> videos;
}
