package org.petrowich.gallerychecker.models.fetches;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.models.AbstractModel;
import org.petrowich.gallerychecker.models.fetches.enums.FetchAim;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.min;

@Setter
@Getter
@Accessors(chain = true)
@Entity(name = "Fetch")
@Table(name = "t_fetches")
public class Fetch extends AbstractModel<Integer> {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq_fetches", sequenceName = "seq_fetches", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_fetches")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "tube_id", referencedColumnName = "id")
    private Tube tube;

    @Column(name = "fetched_galleries")
    private Integer fetchedGalleriesNumber;

    @Transient
    private List<FetchInvocation> fetchInvocations;

    @Column(name = "fetch_datetime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime fetchDateTime;

    @Column(name = "fetch_aim")
    private FetchAim fetchAim;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    public Fetch setId(Integer id) {
        this.id = id;
        this.fetchInvocations.forEach(fetchInvocation -> fetchInvocation.setFetchId(id));
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage.substring(0, min(errorMessage.length(),2048));
    }

    @Override
    public String toString() {
        return String.format("Fetch(from %s status: %s)", tube.getHost(), status);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Fetch entity = (Fetch) object;

        return Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
