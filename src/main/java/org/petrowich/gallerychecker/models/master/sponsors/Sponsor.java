package org.petrowich.gallerychecker.models.master.sponsors;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.petrowich.gallerychecker.models.AbstractModel;

import javax.persistence.*;
import java.util.Objects;

@Setter
@Getter
@Accessors(chain = true)
@Entity(name = "Sponsor")
@Table(name = "t_sponsors")
@Immutable
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY, region = "sponsors")
public class Sponsor extends AbstractModel<Short> {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq_sponsors", sequenceName = "seq_sponsors", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sponsors")
    private Short id;

    @Column(name = "name")
    private String name;

    @Column(name = "active")
    private boolean active;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Sponsor entity = (Sponsor) object;

        return Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
