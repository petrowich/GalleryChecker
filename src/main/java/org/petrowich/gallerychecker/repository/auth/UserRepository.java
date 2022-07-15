package org.petrowich.gallerychecker.repository.auth;

import org.petrowich.gallerychecker.models.users.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserInfo, Integer> {

    UserInfo findByUsername(String username);
}
