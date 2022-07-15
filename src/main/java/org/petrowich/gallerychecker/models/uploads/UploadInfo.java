package org.petrowich.gallerychecker.models.uploads;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.models.AbstractModel;
import org.petrowich.gallerychecker.models.galleries.StoredGallery;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Accessors(chain = true)
@Entity(name = "UploadInfo")
@Table(name = "t_uploads")
public class UploadInfo extends AbstractModel<Integer> {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq_uploads", sequenceName = "seq_uploads", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_uploads")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "upload_datetime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime uploadDateTime;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "uploaded_galleries")
    private Integer uploadedGalleriesNumber;

    @Column(name = "new_uploaded_galleries")
    private Integer newUploadedGalleriesNumber;

    @Transient
    private List<StoredGallery> storedGalleries;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        UploadInfo entity = (UploadInfo) object;

        return Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
