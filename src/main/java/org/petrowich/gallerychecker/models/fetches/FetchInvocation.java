package org.petrowich.gallerychecker.models.fetches;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.petrowich.gallerychecker.models.Model;

import javax.persistence.*;
import java.util.Objects;

import static java.lang.Math.min;

@Setter
@Getter
@Accessors(chain = true)
@Entity(name = "FetchInvocation")
@Table(name = "t_fetch_invocations")
@IdClass(FetchInvocationId.class)
public class FetchInvocation implements Model {

    @Id
    @Column(name = "fetch_id", insertable = false, updatable = false)
    private Integer fetchId;

    @Id
    @Column(name = "invocation_number", insertable = false, updatable = false)
    private Integer invocationNumber;

    @Transient
    private Fetch fetch;

    @Column(name = "url")
    private String fetchUrl;

    @Column(name = "fetched_galleries")
    private Integer fetchedGalleries;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    @Transient
    private String payload;

    public FetchInvocation setFetch(Fetch fetch) {
        this.fetch = fetch;
        this.fetchId = fetch.getId();
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage.substring(0, min(errorMessage.length(),2048));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        FetchInvocation entity = (FetchInvocation) object;

        return Objects.equals(this.fetchId, entity.fetchId)
                && Objects.equals(this.invocationNumber, entity.invocationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fetchId, this.invocationNumber);
    }
}
