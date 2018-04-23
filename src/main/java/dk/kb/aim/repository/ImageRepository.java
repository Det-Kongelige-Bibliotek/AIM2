package dk.kb.aim.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import dk.kb.aim.WordStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import dk.kb.aim.ImageStatus;
import dk.kb.aim.model.Image;

/**
 * Created by dgj on 05-03-2018.
 */
@Repository
public class ImageRepository {
    /** The log.*/
    private static final Logger logger = LoggerFactory.getLogger(ImageRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Image getImage(int id){
        List<Image> rs = queryForImages("SELECT id,path,cumulus_id,category,color,ocr,status FROM images "+
                "WHERE id='"+id+"'");
        if (rs.size() > 0) {
            return rs.get(0);
        } else {
            return null;
        }
    };

    public List<Image> listAllImages() {
        return queryForImages("SELECT id,path,cumulus_id,category,color,ocr,status FROM images");
    }

    public List<Image> listImagesInCategory(String category) {
        return queryForImages("SELECT id,path,cumulus_id,category,color,ocr,status " +
                "FROM images WHERE category ='"+category+"'");
    }

    public List<Image> listImagesInCategoryWithStatus(String category, ImageStatus status) {
        return queryForImages("SELECT id,path,cumulus_id,category,color,ocr,status " +
                "FROM images WHERE category = '"+category+"' "+
                "AND status = '"+status+"'");
    }

    public List<Image> listImagesWithStatus(ImageStatus status) {
        return queryForImages("SELECT id,path,cumulus_id,category,color,ocr,status " +
                "FROM images WHERE status = '"+status+"'");
    }
    
    public int createImage(Image img) {
        final String sql = "INSERT INTO images (path,cumulus_id,color,category,status) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pst =
                                con.prepareStatement(sql, new String[] {"id"});
                        pst.setString(1, img.getPath());
                        pst.setString(2, img.getCumulus_id());
                        pst.setString(3, img.getColor());
                        pst.setString(4, img.getCategory());
                        pst.setString(5, img.getStatus().toString());
                        return pst;
                    }
                },
                keyHolder);
        return (int)keyHolder.getKey();
    }

    public void updateImage(Image img)  {
        Object[] params = {img.getPath(),img.getCumulus_id(),img.getCategory(),img.getStatus(),img.getId()};
        int[] types = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BIGINT};
        jdbcTemplate.update(
                "UPDATE images SET (path,cumulus_id,category,status) = (?,?,?,?) WHERE id = ?",
                params, types);
    }

    public void addWordToImage(int image_id, int word_id, int confidence) {
        Image img = getImage(image_id);
        if (img == null) {
            throw new IllegalArgumentException("Image ('" + image_id + "') does not exits");
        }

        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT category,status FROM words " +
                "WHERE id =" + word_id);
        if (!rows.next()) {
            throw new IllegalStateException("Word ('" + word_id + "') does not exists");
        }

        // reject if status is banned and has correct category

        jdbcTemplate.update("INSERT INTO image_word (image_id, word_id, confidence) VALUES (?,?,?)"
                , image_id,word_id,confidence);
    }

    /*
        Get a list of words associated with an image
        word_id : the id of the word
        status: restrict to images with status (if null no all images are returned)
        limit: the max number of images returned
     */
    public List<Image> wordImages(int word_id, ImageStatus status) {
        return wordImages(word_id,status,10);
    }

    public List<Image> wordImages(int word_id, ImageStatus status, int limit) {
        String sql = "SELECT id,path,cumulus_id,category,color,ocr,status " +
                "FROM images WHERE id in " +
                "(SELECT image_id FROM image_word WHERE word_id = "+word_id+")";
        if (status != null)
                sql += " AND status = '" + status.toString() + "'";
        sql += " ORDER BY id DESC LIMIT "+limit;
        return queryForImages(sql);
    }

    private List<Image> queryForImages(String sql) {
        return jdbcTemplate.query(sql,
                (rs,rowNum) -> new Image(rs.getInt("id"), rs.getString("path"),
                        rs.getString("cumulus_id"), rs.getString("category"),
                        rs.getString("color"),rs.getString("ocr"),
                        ImageStatus.valueOf(rs.getString("status"))));
    }

}
