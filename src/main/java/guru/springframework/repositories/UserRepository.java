package guru.springframework.repositories;

import guru.springframework.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer>{
    User findByUserName(String userName);
}
