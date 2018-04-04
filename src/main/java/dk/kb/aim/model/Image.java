package dk.kb.aim.model;

import java.util.List;

import dk.kb.aim.ImageStatus;

/**
 * Created by dgj on 05-03-2018.
 */
public class Image {
    private int id;
    private String path;
    private String cumulus_id;
    private String category;
    private String color;
    private String ocr;
    private ImageStatus status;
    private List<ImageWord> imageWords;


    public Image(int id, String path, String cumulus_id, String category, String color, String ocr, ImageStatus status) {
        this.id = id;
        this.path = path;
        this.cumulus_id = cumulus_id;
        this.category = category;
        this.color = color;
        this.ocr = ocr;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCumulus_id() {
        return cumulus_id;
    }

    public void setCumulus_id(String cumulus_id) {
        this.cumulus_id = cumulus_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOcr() {
        return ocr;
    }

    public void setOcr(String ocr) {
        this.ocr = ocr;
    }

    public ImageStatus getStatus() {
        return status;
    }

    public void setStatus(ImageStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", cumulus_id='" + cumulus_id + '\'' +
                ", category='" + category + '\'' +
                ", color='" + color + '\'' +
                ", ocr='" + ocr + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
