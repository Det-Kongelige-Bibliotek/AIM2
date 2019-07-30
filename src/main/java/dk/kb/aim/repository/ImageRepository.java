package dk.kb.aim.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.kb.aim.model.WordConfidence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import dk.kb.aim.model.Image;

/**
 * Created by dgj on 05-03-2018.
 */
@Repository
public class ImageRepository {
    /** The database connector.*/
    @Autowired
    private JdbcTemplate jdbcTemplate;
    /** The DB repository for the words.*/
    @Autowired
    private WordRepository wordRepository;

    /** The select query for images, to ensure that all the columns are in the query.*/
    protected static final String SELECT_QUERY = "SELECT id,path,cumulus_id,category,color,ocr,status,isFront "
            + "FROM images";
    
    /**
     * Retrieves the database image object.
     * @param id The ID of the image.
     * @return The requested image.
     */
    public Image getImage(int id) {
        List<Image> rs = queryForImages(SELECT_QUERY + " WHERE id='"+id+"'");
        if (rs.size() > 0) {
            return rs.get(0);
        } else {
            return null;
        }
    }
    
    /**
     * Checks whether a image exists with the given Cumulus ID.
     * @param cumulusId The Cumulus ID to identify.
     * @return Whether or not such a image exists.
     */
    public boolean hasImageWithCumulusId(String cumulusId) {
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM images WHERE cumulus_id='" + cumulusId + "'" , 
                Integer.class);
        return count > 0;
    }
    
    /**
     * Retrieves all the images.
     * @return A list with all the images.
     */
    public List<Image> listAllImages() {
        return queryForImages(SELECT_QUERY);
    }
    
    /**
     * Retrieves the given number of images at the given offset. 
     * They are sorted reverse according the ID of the images.
     * @param count The number of images to fetch.
     * @param offset The offset for the which images to fetch.
     * @return A list with the images.
     */
    public List<Image> listImages(int count, int offset) {
        return queryForImages(SELECT_QUERY + " ORDER BY id DESC LIMIT " + count + " OFFSET " + offset);
    }

    /**
     * Retrieves the mapping between images and their words.
     * @param count The number of images to retrieve.
     * @param offset The offset for the images to retrieve.
     * @return The mapping between the images and their words.
     */
    public Map<Image, List<WordConfidence>> mapImageWords(int count, int offset) {
        List<Image> images = listImages(count, offset);
        Map<Image, List<WordConfidence>> res = new HashMap<>();
        for(Image image : images) {
            res.put(image, wordRepository.getImageWords(image.getId()));
        }

        return res;
    }
    
    /**
     * Retrieves a list with the images of a given category.
     * @param category The category.
     * @return The images of the category.
     */
    public List<Image> listImagesInCategory(String category) {
        return queryForImages(SELECT_QUERY + " WHERE category ='" + category + "'");
    }
    
    /**
     * Retrieves a list with the images of a given category with a given status.
     * @param category The category.
     * @param status The status.
     * @return The list of images with the given status in the given category.
     */
    public List<Image> listImagesInCategoryWithStatus(String category, ImageStatus status) {
        return queryForImages(SELECT_QUERY + " WHERE category = '" + category + "' " +
                " AND status = '" + status + "'");
    }
    
    /**
     * Retrieves the list of images with a given status.
     * @param status The status.
     * @return The list of images with the given status.
     */
    public List<Image> listImagesWithStatus(ImageStatus status) {
        return queryForImages(SELECT_QUERY + " WHERE status = '" + status + "'");
    }
    
    /**
     * Creates a new images in the database.
     * @param img The new image to insert into the database.
     * @return The ID of the new image entry.
     */
    public int createImage(Image img) {
        final String sql = "INSERT INTO images (path,cumulus_id,color,category,status,ocr,isFront) VALUES "
                + "(?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst =
                                con.prepareStatement(sql, new String[] {"id"});
                        pst.setString(1, img.getPath());
                        pst.setString(2, img.getCumulusId());
                        pst.setString(3, img.getColor());
                        pst.setString(4, img.getCategory());
                        pst.setString(5, img.getStatus().toString());
                        pst.setString(6, img.getOcr());
                        pst.setBoolean(7, img.getIsFront());
                        return pst;
                    }
                },
                keyHolder);
        return (int)keyHolder.getKey();
    }
    
    /**
     * Update an existing image in the database.
     * @param img The image to update the entry in the database.
     */
    public void updateImage(Image img)  {
        Object[] params = {img.getPath(),img.getCumulusId(),img.getCategory(),img.getStatus(),img.getOcr(),
                img.getIsFront(), img.getId()};
        int[] types = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BINARY,
                       Types.BIGINT};
        jdbcTemplate.update(
                "UPDATE images SET (path,cumulus_id,category,status,ocr,isFront) = (?,?,?,?,?,?) WHERE id = ?",
                params, types);
    }
    
    /**
     * Adds the connection between a word and an image.
     * Creates the image_word with the confidence of the image having the given word as a label.
     * @param imageId The ID of the image.
     * @param wordId The ID of the word.
     * @param confidence The confidence of the image having the given word as a label.
     */
    public void addWordToImage(int imageId, int wordId, int confidence) {
        Image img = getImage(imageId);
        if (img == null) {
            throw new IllegalArgumentException("Image ('" + imageId + "') does not exits");
        }

        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT category,status FROM words " +
                "WHERE id =" + wordId);
        if (!rows.next()) {
            throw new IllegalStateException("Word ('" + wordId + "') does not exists");
        }

        // reject if status is banned and has correct category

        jdbcTemplate.update("INSERT INTO image_word (image_id, word_id, confidence) VALUES (?,?,?)"
                , imageId,wordId,confidence);
    }

    /**
     * The list of images associated with a given word.
     * Though only the first 10 images returned.
     * @param wordId The ID of the word.
     * @param status The status of the images.
     * @return The list of maximum 10 images associated with a given word.
     */
    public List<Image> wordImages(int wordId, ImageStatus status) {
        return wordImages(wordId,status,10, 0);
    }
    
    /**
     * Get a list of images associated with a given word.
     * @param wordId the id of the word.
     * @param status restrict to images with status (if null no all images are returned).
     * @param limit the max number of images returned.
     * @param offset The offset for the images.
     * @return The list of images associated with a given word.
     */
    public List<Image> wordImages(int wordId, ImageStatus status, int limit, int offset) {
        String sql = SELECT_QUERY + " WHERE id in " +
                "(SELECT image_id FROM image_word WHERE word_id = " + wordId + ")";
        if (status != null) {
            sql += " AND status = '" + status.toString() + "'";
        }
        sql += " ORDER BY id DESC LIMIT " + limit + " OFFSET " + offset;
        return queryForImages(sql);
    }
    
    /**
     * Retrieves the list of images for a given SQL query against the database.
     * @param sql The SQL query to execute for retrieving the list of images.
     * @return The list of images.
     */
    private List<Image> queryForImages(String sql) {
        return jdbcTemplate.query(sql, (rs,rowNum) -> new Image(rs.getInt("id"),
                rs.getString("path"),
                rs.getString("cumulus_id"),
                rs.getString("category"),
                rs.getString("color"),
                rs.getString("ocr"),
                ImageStatus.valueOf(rs.getString("status")),
                rs.getBoolean("isFront")));
    }
    
    /**
     * Removes an image. Both from the images table and all related entries in the image_word table.
     * @param image The image to remove.
     */
    public void removeImage(Image image) {
        jdbcTemplate.execute("DELETE FROM image_word WHERE image_id=" + image.getId());
        jdbcTemplate.execute("DELETE FROM images WHERE id=" + image.getId());
    }
}
