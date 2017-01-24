package guru.springframework.services;

import guru.springframework.domain.User;

/**
 * Created by jt on 12/14/15.
 */
public interface UserService extends CRUDService<User> {

    User findByUserName(String userName);

}
