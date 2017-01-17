package guru.springframework.services;

import java.util.List;

/**
 * Created by jt on 11/14/15.
 */
public interface CRUDService<T> {
    List<?> listAll();

    T getById(Integer id);

    T saveOrUpdate(T domainObject);

    void delete(Integer id);
}
