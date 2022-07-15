package org.petrowich.gallerychecker.models.checks;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.models.AbstractModel;
import org.petrowich.gallerychecker.models.checks.enums.CheckAim;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.lang.Math.min;

@Setter
@Getter
@Accessors(chain = true)
@Entity(name = "Check")
@Table(name = "t_checks")
public class Check extends AbstractModel<Integer> {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq_checks", sequenceName = "seq_checks", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_checks")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;

    @Column(name = "check_datetime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime checkDateTime;

    @Column(name = "check_aim")
    private CheckAim checkAim;

    @Column(name = "checked_galleries")
    private Integer checkedGalleriesNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage.substring(0, min(errorMessage.length(),2048));
    }

    @Override
    public String toString() {
        return String.format("Check(from %s status: %s)", site, status);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Check entity = (Check) object;

        return Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
