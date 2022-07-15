package org.petrowich.gallerychecker.repository.uploads;

import org.petrowich.gallerychecker.models.uploads.UploadInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadInfoRepository extends JpaRepository<UploadInfo, Integer> {
}
