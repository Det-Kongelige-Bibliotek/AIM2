package dk.kb.cumulus.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Transparency;
import java.io.ByteArrayOutputStream;

/**
 * Created by bimo on 19-03-2018.
 */
public class ImageUtils {

    public static void main(String [] args){

        try{
            BufferedImage originalImage = ImageIO.read(new File("c:\\image\\KE030099.tif"));
            ImageIO.write(originalImage, "jpg", new File("c:\\image\\KE030099.jpg"));
            long fileSize = new File("c:\\image\\KE030099.jpg").length();
            //System.out.print("Output image size:");
            //System.out.println(fileSize);
            BufferedImage bm = originalImage;
            while (fileSize > 3999999){
                //System.out.print("Output image size:");
                //System.out.println(fileSize);

                originalImage = ImageIO.read(new File("c:\\image\\KE030099.jpg"));
                bm = resize(originalImage,originalImage.getWidth()-200, originalImage.getHeight()-200);
                ImageIO.write(bm, "jpg", new File("c:\\image\\KE030099.jpg"));
                fileSize = new File("c:\\image\\KE030099.jpg").length();
            }
            ImageIO.write(bm, "jpg", new File("c:\\image\\KE030099.jpg"));

        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static BufferedImage resize(BufferedImage image, int areaWidth, int areaHeight)
    {
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
}
