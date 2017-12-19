package uk.gov.hmcts.bar.api.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository<T,ID extends Serializable> extends JpaRepository<T,ID> {

    default T saveAndRefresh(T t) {
        saveAndFlush(t);
        refresh(t);
        return t;
    }

    void refresh(T t);
}

