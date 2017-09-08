package uk.gov.hmcts.bar.api.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;


@Repository
public class HelloRepository {

    private static final Logger LOG = LoggerFactory.getLogger(HelloRepository.class);

    private final ResourceLoader resourceLoader = null;
    private final ObjectMapper objectMapper = null;

    @PostConstruct
    public void init() {
        LOG.error("DB! What DB?");
    }
}
