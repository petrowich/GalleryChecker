package org.petrowich.gallerychecker.models.fetches;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class FetchInvocationId implements Serializable {

    private Integer fetchId;
    private Integer invocationNumber;

    public FetchInvocationId(Integer fetchId, Integer invocationNumber) {
        this.fetchId = fetchId;
        this.invocationNumber = invocationNumber;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof FetchInvocationId)) {
            return false;
        }

        FetchInvocationId that = (FetchInvocationId) object;

        return Objects.equals(this.fetchId, that.fetchId) &&
                Objects.equals(this.invocationNumber, that.invocationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fetchId, invocationNumber);
    }
}
