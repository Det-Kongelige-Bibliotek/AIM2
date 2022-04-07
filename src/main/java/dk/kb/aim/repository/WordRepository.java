package dk.kb.aim.repository;

import dk.kb.aim.model.Word;
import dk.kb.aim.model.WordConfidence;
import dk.kb.aim.model.WordCount;
import dk.kb.aim.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * Created by dgj on 22-02-2018.
 */
@Repository
public class WordRepository {
    /** The database connection. */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Creates a database entry for a new word.
     * @param word The word to be entered into the database.
     * @return The ID of the new word entry.
     */
    public int createWord(Word word) {
        final String sql = "INSERT INTO words (text_en,text_da,category,status) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
                    PreparedStatement pst = con.prepareStatement(sql, new String[] {"id"});
                    pst.setString(1, word.getTextEn());
                    pst.setString(2, word.getTextDa());
                    pst.setString(3, word.getCategory());
                    pst.setString(4, word.getStatus().toString());
                    return pst;
                },
                keyHolder);
        return (int)keyHolder.getKey();
    }
    
    /**
     * Retrieves the word of a given ID.
     * @param id The ID of the word.
     * @return The word, or null if no such word exists.
     */
    public Word getWord(int id){
        List<Word> rs = queryForWords("SELECT id,text_en,text_da,category,status FROM words "+
                "WHERE id='"+id+"'");
        if (rs.size() > 0) {
            return rs.get(0);
        } else {
            return null;
        }
    }

    /**
     * Retrieves a word by the english text column and the category.
     * @param textEn The english text value to search for.
     * @param category The category of the word.
     * @return The word, or null if no such word exists.
     */
    public Word getWordByText(String textEn, String category) {
        List<Word> rs = queryForWords("SELECT id,text_en,text_da,category,status FROM words "+
                "WHERE text_en='" + textEn + "' AND category ='" + category + "'");
        if (rs.size() > 0) {
            return rs.get(0);
        } else {
            return null;
        }
    }
    
    /**
     * Update a given word. If another word exists along the given word, then merge this word into the other, remove it
     * and return the other word.
     * @param word The word to update.
     * @return The word.
     */
    public Word updateWord(Word word) {
        if(hasDuplicateWord(word)) {
            Word primary = getWordByText(word.getTextEn(), word.getCategory());
            mergeWords(word, primary);
            word.setId(primary.getId());
            return primary;
        } else {
            jdbcTemplate.update(
                    "UPDATE words SET (text_en,text_da,category,status) = (?,?,?,?) WHERE id = ?",
                    word.getTextEn(), word.getTextDa(), word.getCategory(), word.getStatus().toString(), word.getId());
            return word;
        }
    }

    /**
     * Check whether a word has a duplicate within a category - needed before update.
     * @param word The word to test whether it has a duplicate.
     * @return Whether a duplicate exists.
     */
    protected boolean hasDuplicateWord(Word word) {
        List<Word> words = queryForWords("SELECT id,text_en,text_da,category,status FROM words "+
                "WHERE text_en='"+ word.getTextEn() + "' AND category='" + word.getCategory() + "'");
        for(Word w : words) {
            if(w.getId() != word.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Merge one word into another.
     * Thus moving all image-word relations to the other word, and then removing the from word.
     * @param from The word to merge from - and then remove.
     * @param to The word to move to.
     */
    protected void mergeWords(Word from, Word to) {
        String updateSql = "UPDATE image_word SET word_id = ? WHERE word_id = ?";
        jdbcTemplate.update(updateSql, new Object[] {to.getId(), from.getId()});

        String deleteSql = "DELETE FROM words WHERE id = ?";
        jdbcTemplate.update(deleteSql, from.getId());
    }

    /**
     * Retrieves all the words.
     * @param orderBy The name of the column to sort by.
     * @param ascending Whether to sort in ascending.
     * @return The list with all the words.
     */
    public List<WordCount> allWordCounts(String orderBy, boolean ascending) {
        return getWordCounts(null, orderBy, ascending);
    }
    
    /**
     * Retrieves all the words with a given status.
     * @param status The status.
     * @param orderBy The name of the column to sort by.
     * @param ascending Whether to sort in ascending.
     * @return The list with all the words with the given status.
     */
    public List<WordCount> allWordCountsWithStatus(WordStatus status, String orderBy, boolean ascending) {
        return getWordCounts("WHERE status = '" + status + "'", orderBy, ascending);
    }
    
    /**
     * Retrieves all the words in a given category.
     * @param category The category for the words.
     * @param orderBy The name of the column to sort by.
     * @param ascending Whether to sort in ascending.
     * @return The list of words for a given category.
     */
    public List<WordCount> allWordCountsInCategory(String category, String orderBy, boolean ascending) {
        return getWordCounts("WHERE category = '" + category + "'", orderBy, ascending);
    }
    
    /**
     * Retrieves the list of words with a given category and status.
     * @param category The category.
     * @param status The status.
     * @param orderBy The name of the column to sort by.
     * @param ascending Whether to sort in ascending.
     * @return The list of words with the given status and in the given category.
     */
    public List<WordCount> allWordCountsInCategoryWithStatus(String category, WordStatus status, String orderBy,
                                                             boolean ascending) {
        return getWordCounts("WHERE category = '" + category + "' AND status = '" + status + "'",
            orderBy, ascending);
    }
    
    /**
     * Whether or not a word with the given english text is accepted for a given categorty.
     * @param textEn The english text.
     * @param category The categorty.
     * @return Whether or not such a word is accepted.
     */
    public boolean isAcceptedFor(String textEn, String category) {
        String sql = "SELECT count(*) FROM words WHERE text_en = ? AND category = ? AND status = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[] {textEn, category, WordStatus.ACCEPTED.toString()}, 
                Integer.class);
        return count > 0;
    }
    
    /**
     * Whether or not a word with the given english text is rejected for a given categorty.
     * @param textEn The english text.
     * @param category The categorty.
     * @return Whether or not such a word is rejected.
     */
    public boolean isRejectedFor(String textEn, String category) {
        String sql = "SELECT count(*) FROM words WHERE text_en = ? AND category = ? AND status = ? ";
        int count = jdbcTemplate.queryForObject(sql, new Object[] {textEn, category, WordStatus.REJECTED.toString()}, 
                Integer.class);
        return count > 0;
    }
    
    /**
     * Retrieves all the words associated with a given image.
     * @param imageId The Id of the image.
     * @return The list of words associated with the image.
     */
    public List<WordConfidence> getImageWords(int imageId) {
        String sql = "SELECT * " +
                "from image_word i INNER JOIN words w ON i.word_id = w.id WHERE " +
                "i.image_id = " + imageId;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new WordConfidence(
                rs.getInt("id"), rs.getString("text_en"), rs.getString("text_da"), rs.getString("category"), 
                WordStatus.valueOf(rs.getString("status")), rs.getInt("confidence")));
    }
    
    /**
     * Retrieves the list of words with a given status associated with a given image.
     * @param imageId The image.
     * @param status The status for the words.
     * @return The list of words with the status associated with the image.
     */
    public List<WordConfidence> getImageWords(int imageId, WordStatus status) {
        String sql = "SELECT * " +
                "from image_word i INNER JOIN words w ON i.word_id = w.id WHERE " +
                "i.image_id = " + imageId + " AND " +
                "w.status='" + status + "'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new WordConfidence(
                rs.getInt("id"), rs.getString("text_en"), rs.getString("text_da"), rs.getString("category"), 
                WordStatus.valueOf(rs.getString("status")), rs.getInt("confidence")));
    }
    
    /**
     * Retrieves all the different categories for the words.
     * @return The list of categories.
     */
    public List<String> getCategories() {
        String sql = "select DISTINCT category from words";
        return jdbcTemplate.queryForList(sql,String.class);
    }
    
    /**
     * The list of words retrieves by a given SQL query.
     * @param sql The SQL query.
     * @return The list of words retrieved by the SQL query.
     */
    protected List<Word> queryForWords(String sql) {
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Word(rs.getInt("id"), rs.getString("text_en"),
                rs.getString("text_da"), rs.getString("category"), WordStatus.valueOf(rs.getString("status"))));
    }

    /**
     * Retrieves the word counts for all words with the given restriction.
     * @param where The restriction for which words to extract.
     * @param orderBy The name of the column to sort by.
     * @param ascending Whether to sort in ascending.
     * @return The word counts.
     */
    protected List<WordCount> getWordCounts(String where, String orderBy, boolean ascending) {
        String sql = "SELECT words.id, words.text_en, words.text_da, words.category, words.status, "
                + "COUNT(words.id) AS count "
                + "FROM words INNER JOIN image_word ON words.id = image_word.word_id ";
        if(StringUtils.hasValue(where)) {
            sql += where;
        }
        sql += " GROUP BY words.id ORDER BY words." + orderBy + " " + (ascending ? "ASC" : "DESC");
        return jdbcTemplate.query(sql, (rs, rowNum) -> new WordCount(rs.getInt("id"), rs.getString("text_en"),
                rs.getString("text_da"), rs.getString("category"),
                WordStatus.valueOf(rs.getString("status")), rs.getInt("count")));
    }
}
