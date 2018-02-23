package dk.kb.cumulus.repository;

import dk.kb.cumulus.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by dgj on 22-02-2018.
 */
@Repository
public class WordRepository {



    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Word> allWords() {
        List<Word> result = jdbcTemplate.query(
                "SELECT id,text_en,text_da,status from words",
                (rs,rowNum) -> new Word(rs.getInt("id"), rs.getString("text_en"),
                        rs.getString("text_da"), rs.getString("status"))
        );
        return result;
    }
}
