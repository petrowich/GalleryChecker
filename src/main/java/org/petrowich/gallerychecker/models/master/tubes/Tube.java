package org.petrowich.gallerychecker.models.master.tubes;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.petrowich.gallerychecker.models.AbstractModel;
import org.petrowich.gallerychecker.models.master.JobOwner;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Accessors(chain = true)
@Entity(name = "Tubes")
@Table(name = "t_tubes")
@Cacheable
@Immutable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "tubes")
public class Tube extends AbstractModel<Integer> implements JobOwner {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq_tubes", sequenceName = "seq_tubes", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tubes")
    private Integer id;

    @Column(name = "host")
    private String host;

    @OneToMany(mappedBy = "tube")
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "embed_video_hosts")
    private List<TubeEmbedVideoHost> embedVideoHosts;

    @Transient
    private Integer activeTubeGalleriesNumber;

    @Transient
    private Integer inactiveTubeGalleriesNumber;

    @Transient
    private Integer storedGalleriesNumber;

    @Transient
    private Integer uncheckedStoredGalleriesNumber;

    @Transient
    private Integer unavailableStoredGalleriesNumber;

    @Override
    public String toString() {
        return String.format("tube:\"%s\"(id=%d)", host, id);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Tube entity = (Tube) object;

        return Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
