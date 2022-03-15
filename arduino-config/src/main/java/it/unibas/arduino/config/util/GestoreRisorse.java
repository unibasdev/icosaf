package it.unibas.arduino.config.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.batik.anim.dom.SVG12DOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;

public class GestoreRisorse {

    public static URL getImageURL(String fileName) {
        URL imageUrl = GestoreRisorse.class.getResource("/images/" + fileName);
        if (imageUrl == null) {
            throw new IllegalArgumentException("Impossibile caricare l'immagine " + fileName);
        }
        return imageUrl;
    }

    public static InputStream getImageStream(String fileName) {
        InputStream stream = GestoreRisorse.class.getResourceAsStream("/images/" + fileName);
        if (stream == null) {
            throw new IllegalArgumentException("Impossibile caricare l'immagine " + fileName);
        }
        return stream;
    }

    public static Image getImage(String fileName) {
        return Toolkit.getDefaultToolkit().getImage(getImageURL(fileName));
    }

    public static BufferedImage getBufferedImage(String fileName) {
        try {
            return ImageIO.read(getImageURL(fileName));
        } catch (IOException ex) {
            throw new IllegalArgumentException("Impossibile caricare l'immagine " + fileName + ": " + ex.getLocalizedMessage());
        }
    }

    public static ImageIcon getSmallSVGIcon(String fileName) {
        return getSVGIcon(fileName, 18);
    }

    public static ImageIcon getSVGIcon(String fileName) {
        return getSVGIcon(fileName, 20);
    }

    public static ImageIcon getLargeSVGIcon(String fileName) {
        return getSVGIcon(fileName, 48);
    }

    public static ImageIcon getSVGIcon(String fileName, float size) {
        try {
            SVGTranscoder transcoder = new SVGTranscoder();
            TranscodingHints hints = new TranscodingHints();
            hints.put(ImageTranscoder.KEY_WIDTH, size);
            hints.put(ImageTranscoder.KEY_HEIGHT, size);
            hints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION, SVG12DOMImplementation.getDOMImplementation());
            hints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI, SVGConstants.SVG_NAMESPACE_URI);
            hints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
            transcoder.setTranscodingHints(hints);
            transcoder.transcode(new TranscoderInput(getImageStream(fileName)), null);
            BufferedImage image = transcoder.getImage();
            return new ImageIcon(image);
        } catch (TranscoderException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Impossibile caricare l'immagine svg " + fileName + ": " + ex.getLocalizedMessage());
        }
    }

}
