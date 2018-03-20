package dk.kb.cumulus.utils;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class LoadImageApp{
    public static void main(String args[])throws IOException{
        BufferedImage image = null;
        File f = null;

        //read image file
        try{
            f = new File("KE030001.jpg");
            image = ImageIO.read(f);
        }catch(IOException e){
            System.out.println("Error: "+e);
        }

        //write image
        try{
            f = new File("D:\\Image\\Output.jpg");
            ImageIO.write(image, "jpg", f);
        }catch(IOException e){
            System.out.println("Error: "+e);
        }
    }//main() ends here
}//class ends here