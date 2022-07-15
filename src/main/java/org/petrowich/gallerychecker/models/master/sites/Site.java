package org.petrowich.gallerychecker.models.master.sites;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.petrowich.gallerychecker.models.AbstractModel;
import org.petrowich.gallerychecker.models.master.JobOwner;

import javax.persistence.*;
import java.util.Objects;

@Setter
@Getter
@Accessors(chain = true)
@Entity(name = "Site")
@Immutable
@Table(name = "t_sites")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "sites")
public class Site extends AbstractModel<Integer> implements JobOwner {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq_sites", sequenceName = "seq_sites", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sites")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "host")
    private String host;

    @Column(name = "active")
    private boolean active;

    @Transient
    private Integer tubesNumber;

    @Transient
    private Integer galleriesNumber;
    
    @Transient
    private Integer uncheckedGalleriesNumber;

    @Transient
    private Integer availableCheckedGalleriesNumber;

    @Transient
    private Integer unavailableCheckedGalleriesNumber;

    @Override
    public String toString() {
        return String.format("site:\"%s\"(id=%d)", host, id);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Site entity = (Site) object;

        return Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
