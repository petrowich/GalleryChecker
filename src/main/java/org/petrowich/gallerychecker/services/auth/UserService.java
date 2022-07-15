package org.petrowich.gallerychecker.services.auth;

import lombok.extern.log4j.Log4j2;
import org.petrowich.gallerychecker.models.users.UserInfo;
import org.petrowich.gallerychecker.repository.auth.UserRepository;
import org.petrowich.gallerychecker.services.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class UserService extends AbstractService<UserInfo, Integer, UserRepository> {

    @Autowired
    public UserService(UserRepository userRepository) {
        super(userRepository);
    }

    public UserInfo findByUsername(String username) {
        return getRepository().findByUsername(username);
    }
}
