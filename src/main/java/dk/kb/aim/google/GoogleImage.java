package dk.kb.aim.google;

import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;
import dk.kb.aim.exception.ArgumentCheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Container class for the google image.
 *
 * To avoid handling multiple classes with the same simple name (e.g. Image in our model, and the google Image).
 */
public class GoogleImage {

    /** The image.*/
    protected final Image image;

    /**
     * Reads a file as a Google Vision image object.
     * @param imageFile The the file to read.
     * @throws IOException If it fails to read the image file.
     */
    public GoogleImage(File imageFile) throws IOException {
        ArgumentCheck.checkExistsNormalFile(imageFile, "File imageFile");
        try (InputStream in = new FileInputStream(imageFile)) {
            ByteString imgBytes = ByteString.readFrom(in);
            image = Image.newBuilder().setContent(imgBytes).build();
        }
    }

    /**
     * @return The Google image.
     */
    public Image getImage() {
        return image;
    }
}
