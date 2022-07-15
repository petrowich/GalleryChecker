package org.petrowich.gallerychecker.models.galleries;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.models.AbstractModel;
import org.petrowich.gallerychecker.models.fetches.Fetch;
import org.petrowich.gallerychecker.models.master.tubes.Tube;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@Accessors(chain = true)
@Entity(name = "TubeGallery")
@Table(name = "t_tube_galleries")
@ToString
public class TubeGallery extends AbstractModel<Integer> {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "seq_tube_galleries", sequenceName = "seq_tube_galleries", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tube_galleries")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "fetch_id", referencedColumnName = "id")
    private Fetch fetch;

    @ManyToOne
    @JoinColumn(name = "tube_id", referencedColumnName = "id")
    private Tube tube;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "url")
    private String url;

    @Column(name = "thumb_url")
    private String thumbUrl;

    @Column(name = "embed_code")
    private String embedCode;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "description")
    private String description;

    @Column(name = "tags")
    private String tags;

    @Column(name = "model")
    private String model;

    @Column(name = "active")
    private boolean active;

    @Column(name = "video_date")
    private LocalDate videoDate;

    @Column(name = "change_datetime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime changeDateTime;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        TubeGallery entity = (TubeGallery) object;

        return Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
