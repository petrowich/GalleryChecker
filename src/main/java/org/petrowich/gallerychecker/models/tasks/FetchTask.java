package org.petrowich.gallerychecker.models.tasks;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.models.Model;
import org.petrowich.gallerychecker.models.fetches.enums.FetchAim;
import org.petrowich.gallerychecker.models.master.tubes.Tube;

import java.time.LocalDateTime;

@Setter
@Getter
@Accessors(chain = true)
public class FetchTask implements Model {
    private String id;
    private String username;
    private Tube tube;
    private FetchAim fetchAim;
    private boolean once;
    private LocalDateTime lastFireDateTime;
    private LocalDateTime nextFireDateTime;
    private Integer periodHours;
    private String status;
}
