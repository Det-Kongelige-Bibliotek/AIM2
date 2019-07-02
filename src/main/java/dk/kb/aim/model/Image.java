package dk.kb.aim.model;

import java.util.List;

import dk.kb.aim.repository.ImageStatus;

/**
 * Created by dgj on 05-03-2018.
 */
public class Image {
    /** The ID of the image.*/
    private int id;
    /** The path to the thumbnail of the image.*/
    private String path;
    /** The Cumulus ID; Value of the Cumulus field: Record Name. */
    private String cumulusId;
    /** The category of the image.*/
    private String category;
    /** The color of the image.*/
    private String color;
    /** The OCR text of the image.*/
    private String ocr;
    /** The status of the image.*/
    private ImageStatus status;
    /** The list of word relations for the image.*/
    private List<ImageWord> imageWords;
    /** Whether or not this image is the front (and thus not the back) of an image.*/
    private Boolean isFront;
    
    /**
     * Constructor.
     * @param id The ID of the image.
     * @param path The path to the thumbnail of the image.
     * @param cumulusId The ID for the related Cumulus record.
     * @param category The category of the image.
     * @param color The color of the image.
     * @param ocr The OCR text of the image.
     * @param status The status of the image.
     */
    public Image(int id, String path, String cumulusId, String category, String color, String ocr, ImageStatus status) {
        this.id = id;
        this.path = path;
        this.cumulusId = cumulusId;
        this.category = category;
        this.color = color;
        this.ocr = ocr;
        this.status = status;
    }
    
    /**
     * @return The ID of the image.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Set the ID of the image.
     * @param id The new ID of the image.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return The path to the thumbnail of the image.
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Sets a new path to the thumbnail of the image.
     * @param path The new path for the thumbnail of the image.
     */
    public void setPath(String path) {
        this.path = path;
    }
    
    /**
     * @return The ID for the related Cumulus record.
     */
    public String getCumulusId() {
        return cumulusId;
    }
    
    /**
     * Set a new ID for the related Cumulus record.
     * @param cumulusId The new ID for the related Cumulus record.
     */
    public void setCumulus_id(String cumulusId) {
        this.cumulusId = cumulusId;
    }
    
    /**
     * @return The category of the image.
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Set a new category of the image.
     * @param category The new category of the image.
     */
    public void setCategory(String category) {
        this.category = category;
    }
    
    /**
     * @return The color of the image.
     */
    public String getColor() {
        return color;
    }
    
    /**
     * Set a new color of the image.
     * @param color The new color of the image.
     */
    public void setColor(String color) {
        this.color = color;
    }
    
    /**
     * @return The OCR text of the image.
     */
    public String getOcr() {
        return ocr;
    }
    
    /**
     * Set a new OCR text of the image.
     * @param ocr The new OCR text of the image.
     */
    public void setOcr(String ocr) {
        this.ocr = ocr;
    }
    
    /**
     * @return The status of the image.
     */
    public ImageStatus getStatus() {
        return status;
    }
    
    /**
     * Set a new status of the image.
     * @param status The new status of the image.
     */
    public void setStatus(ImageStatus status) {
        this.status = status;
    }
    
    /**
     * @return The list of word relations for the image.
     */
    public List<ImageWord> getImageWords() {
        return imageWords;
    }
    
    /**
     * Set a new list of word relations for the image.
     * @param imageWords The new list of word relations for the image.
     */
    public void setImageWords(List<ImageWord> imageWords) {
        this.imageWords = imageWords;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", cumulus_id='" + cumulusId + '\'' +
                ", category='" + category + '\'' +
                ", color='" + color + '\'' +
                ", ocr='" + ocr + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
