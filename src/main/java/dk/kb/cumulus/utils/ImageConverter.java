package dk.kb.cumulus.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dk.kb.cumulus.Configuration;

import java.awt.Transparency;
import java.io.ByteArrayOutputStream;

/**
 * Created by bimo on 19-03-2018.
 */
@Component
public class ImageConverter {

    /** The name for JPG.*/
    public final static String JPG_NAME = "jpg";
    
    @Autowired
    protected Configuration conf;

    /**
     * Converts a TIFF image into JPG and puts it into the JPG folder.
     * It will continuously downscale the image until it is less than 3 MB in size.
     * @param tiffFile The tiff image.
     * @return The converted JPG file.
     * @throws IOException If something goes wrong with READ/WRITE or converting the image.
     */
    public File convertTiff(File tiffFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(tiffFile);
        
        File res = new File(conf.getJpegFolder(), getJpgName(tiffFile.getName()));
        ImageIO.write(originalImage, JPG_NAME, res);
        BufferedImage jpgImage = ImageIO.read(res);

        BufferedImage bm = jpgImage;
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        long fileSize = res.length();
        while (fileSize > conf.getJpegSizeLimit()) {
            bm = resize(jpgImage,jpgImage.getWidth()-200, jpgImage.getHeight()-200);
            ImageIO.write(bm, JPG_NAME, tmp);
            tmp.close();
            fileSize = tmp.size();
            tmp.reset();
            jpgImage = bm;
        }
        ImageIO.write(bm, JPG_NAME, res);

        return res;
    }

    /**
     * Method for resizing an image.
     * @param image The image.
     * @param areaWidth The new width for the image.
     * @param areaHeight The new height for the image.
     * @return The new resized image.
     */
    public BufferedImage resize(BufferedImage image, int areaWidth, int areaHeight) {
        float scaleX = (float) areaWidth / image.getWidth();
        float scaleY = (float) areaHeight / image.getHeight();
        float scale = Math.min(scaleX, scaleY);
        int w = Math.round(image.getWidth() * scale);
        int h = Math.round(image.getHeight() * scale);

        int type = image.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

        boolean scaleDown = scale < 1;

        if (scaleDown) {
            // multi-pass bilinear div 2
            int currentW = image.getWidth();
            int currentH = image.getHeight();
            BufferedImage resized = image;
            while (currentW > w || currentH > h) {
                currentW = Math.max(w, currentW / 2);
                currentH = Math.max(h, currentH / 2);

                BufferedImage temp = new BufferedImage(currentW, currentH, type);
                Graphics2D g2 = temp.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(resized, 0, 0, currentW, currentH, null);
                g2.dispose();
                resized = temp;
            }
            return resized;
        } else {
            Object hint = scale > 2 ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR;

            BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(image, 0, 0, w, h, null);
            g2.dispose();
            return resized;
        }
    }
    
    /**
     * Finds the JPG name for a file:
     * E.g. 'image.tiff' will be 'image.jpg'.
     * 
     * @param fileName The name of the file.
     * @return The name of the corresponding jpg file.
     */
    protected String getJpgName(String fileName) {
        int index = fileName.lastIndexOf(".");
        if(index > 0) {
            return fileName.substring(0, index) + "." + JPG_NAME;
        } else {
            return fileName + "." + JPG_NAME;
        }
    }
}
