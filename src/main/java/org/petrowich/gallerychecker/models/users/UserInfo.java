package org.petrowich.gallerychecker.models.users;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.petrowich.gallerychecker.models.Model;
import org.petrowich.gallerychecker.models.users.enums.UserRole;
import org.petrowich.gallerychecker.models.users.enums.UserStatus;

import javax.persistence.*;

@Getter
@Setter
@Accessors(chain = true)
@Entity(name = "User")
@Table(name = "t_users", schema = "auth")
public class UserInfo implements Model {
    @Id
    @SequenceGenerator(name = "auth.seq_users", sequenceName = "auth.seq_users", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth.seq_users")
    private Integer id;
    @Column(name="username")
    private String username;
    @Column(name="password")
    private String password;
    @Column(name="email")
    private String email;
    @Column(name="role")
    private UserRole userRole;
    @Column(name="status")
    private UserStatus userStatus;
}