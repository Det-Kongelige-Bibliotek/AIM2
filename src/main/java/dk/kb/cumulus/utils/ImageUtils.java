package dk.kb.cumulus.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Created by bimo on 19-03-2018.
 */
public class ImageUtils {

    public static void main(String [] args){

        try{
            BufferedImage originalImage = ImageIO.read(new File("c:\\image\\KE030099.tif"));
            ImageIO.write(originalImage, "jpg", new File("c:\\image\\KE030099.jpg"));
            long fileSize = new File("c:\\image\\KE030099.jpg").length();
            System.out.println(originalImage.getWidth());
            System.out.println(fileSize);
            if (fileSize > 3999999){
                originalImage = ImageIO.read(new File("c:\\image\\KE030099.jpg"));

                int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

                BufferedImage resizeImageJpg = resizeImage(originalImage, type, originalImage.getWidth()-100, originalImage.getHeight()-100);
                System.out.println(originalImage.getWidth());

                ImageIO.write(resizeImageJpg, "jpg", new File("c:\\image\\KE000001_resized.jpg"));

                BufferedImage resizeImageHintJpg = resizeImageWithHint(originalImage, type, originalImage.getWidth()-100, originalImage.getHeight()-100);
                ImageIO.write(resizeImageHintJpg, "jpg", new File("c:\\image\\KE000001_hint.jpg"));
            }



        }catch(IOException e){
            System.out.println(e.getMessage());
        }

    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type, int  IMG_WIDTH, int IMG_HEIGHT){
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();

        return resizedImage;
    }

    private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT){

        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }
}
