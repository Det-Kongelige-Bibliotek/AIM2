package dk.kb.cumulus.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by dgj on 05-03-2018.
 */
@Repository
public class ImageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

}
