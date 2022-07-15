package org.petrowich.gallerychecker.models.galleries;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.models.*;
import org.petrowich.gallerychecker.models.master.sites.Site;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
import org.petrowich.gallerychecker.models.uploads.UploadInfo;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.SequenceGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.lang.Math.min;

@Setter
@Getter
@Accessors(chain = true)
@Entity(name = "StoredGallery")
@Table(name = "t_stored_galleries")
@ToString
public class StoredGallery extends AbstractModel<Integer> {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq_stored_galleries", sequenceName = "seq_stored_galleries", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_stored_galleries")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "upload_id", referencedColumnName = "id")
    private UploadInfo uploadInfo;

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private Site site;

    @ManyToOne
    @JoinColumn(name = "tube_id", referencedColumnName = "id")
    private Tube tube;

    @Column(name = "url")
    private String url;

    @Column(name = "gallery_datetime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime galleryDateTime;

    @Column(name = "thumb_url")
    private String thumbUrl;

    @Column(name = "embed_code")
    private String embedCode;

    @Column(name = "embed_url")
    private String embedUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "checked")
    private boolean checked;

    @Column(name = "available")
    private boolean available;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "status_message")
    private String statusMessage;

    @Column(name = "error")
    private boolean error;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "check_datetime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime checkDateTime;

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

        StoredGallery entity = (StoredGallery) object;

        return Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
