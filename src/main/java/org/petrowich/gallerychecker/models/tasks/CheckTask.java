package org.petrowich.gallerychecker.models.tasks;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.models.Model;
import org.petrowich.gallerychecker.models.checks.enums.CheckAim;
import org.petrowich.gallerychecker.models.master.sites.Site;

import java.time.LocalDateTime;

@Setter
@Getter
@Accessors(chain = true)
public class CheckTask implements Model {
    private String id;
    private String username;
    private Site site;
    private CheckAim checkAim;
    private boolean once;
    private LocalDateTime lastFireDateTime;
    private LocalDateTime nextFireDateTime;
    private Integer periodDays;
    private String status;
}