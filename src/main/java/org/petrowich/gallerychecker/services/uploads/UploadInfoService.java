package org.petrowich.gallerychecker.services.uploads;

import org.petrowich.gallerychecker.models.uploads.UploadInfo;
import org.petrowich.gallerychecker.repository.uploads.UploadInfoRepository;
import org.petrowich.gallerychecker.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadInfoService extends AbstractService<UploadInfo, Integer, UploadInfoRepository> {
    @Autowired
    public UploadInfoService(UploadInfoRepository uploadInfoRepository) {
        super(uploadInfoRepository);
    }
}
