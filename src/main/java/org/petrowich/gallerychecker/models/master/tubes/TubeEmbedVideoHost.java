package org.petrowich.gallerychecker.models.master.tubes;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Accessors(chain = true)
@Entity(name = "TubeEmbedVideoHost")
@Table(name = "t_tube_embed_video_hosts")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "embed_video_hosts")
public class TubeEmbedVideoHost implements Serializable {

    @ManyToOne
    @JoinColumn(name = "tube_id", referencedColumnName = "id")
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "tubes")
    private Tube tube;

    @Id
    @Column(name = "embed_video_host")
    private String embedVideoHost;
}
