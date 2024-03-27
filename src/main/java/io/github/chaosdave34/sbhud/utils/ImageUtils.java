package io.github.chaosdave34.sbhud.utils;

import com.sun.jna.Platform;
import io.github.chaosdave34.sbhud.SBHUD;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImageUtils {
    public static ByteBuffer[] load(final String filepath) {
        return load(new File(filepath));
    }

    public static ByteBuffer[] load(final File file) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (final IOException e) {
            SBHUD.logger.error("Error while reading image file: " + e.getMessage());
        }

        return createByteBuffers(image);
    }

    public static ByteBuffer[] load(final InputStream inputStream) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(inputStream);
        } catch (final IOException e) {
            SBHUD.logger.error("Error while reading image file: " + e.getMessage());
        }

        return createByteBuffers(image);
    }

    private static ByteBuffer[] createByteBuffers(BufferedImage image) {
        final ByteBuffer[] buffers;
        if (Platform.isWindows()) {
            buffers = new ByteBuffer[2];
            buffers[0] = loadInstance(image, 16);
            buffers[1] = loadInstance(image, 32);
        } else if (Platform.isMac()) {
            buffers = new ByteBuffer[1];
            buffers[0] = loadInstance(image, 128);
        } else {
            buffers = new ByteBuffer[1];
            buffers[0] = loadInstance(image, 32);
        }
        return buffers;
    }

    private static ByteBuffer loadInstance(final BufferedImage image, final int dimension) {
        final BufferedImage scaledIcon = new BufferedImage(dimension, dimension, BufferedImage.TYPE_INT_ARGB_PRE);
        final Graphics2D g = scaledIcon.createGraphics();
        final double ratio = getIconRatio(image, scaledIcon);
        final double width = image.getWidth() * ratio;
        final double height = image.getHeight() * ratio;
        g.drawImage(image, (int) ((scaledIcon.getWidth() - width) / 2),
                (int) ((scaledIcon.getHeight() - height) / 2), (int) (width),
                (int) (height), null);
        g.dispose();

        return convertToByteBuffer(scaledIcon);
    }

    private static double getIconRatio(final BufferedImage src, final BufferedImage icon) {
        double ratio;
        ratio = (double) (icon.getWidth()) / src.getWidth();

        if (src.getHeight() > icon.getHeight()) {
            final double r2 = (double) (icon.getHeight()) / src.getHeight();
            if (r2 < ratio) ratio = r2;
        } else {
            final double r2 = (int) (icon.getHeight() / src.getHeight());
            if (r2 < ratio) ratio = r2;
        }
        return ratio;
    }

    public static ByteBuffer convertToByteBuffer(final BufferedImage image) {
        final byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
        int counter = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int colorSpace = image.getRGB(j, i);
                buffer[counter] = (byte) ((colorSpace << 8) >> 24);
                buffer[counter + 1] = (byte) ((colorSpace << 16) >> 24);
                buffer[counter + 2] = (byte) ((colorSpace << 24) >> 24);
                buffer[counter + 3] = (byte) (colorSpace >> 24);
                counter += 4;
            }
        }
        return ByteBuffer.wrap(buffer);
    }
}
