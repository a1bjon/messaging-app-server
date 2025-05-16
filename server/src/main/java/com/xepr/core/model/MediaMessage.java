package com.xepr.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

@Entity
@Table(name = "media_message_model")
@Getter
@Setter
@ToString
public class MediaMessage extends Message implements Compressible {

    @Lob
    private byte[] media;

    @Override
    public byte[] compress(byte[] data, double scale, double quality) throws IOException {
        if (data == null) {
            throw new NullPointerException("Cannot perform compression on null data");
        }

        if (scale <= 0.0) {
            scale = Compressible.DEFAULT_SCALE;
        }

        if (quality <= 0.0) {
            quality = Compressible.DEFAULT_QUALITY;
        }

        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             ByteArrayInputStream is = new ByteArrayInputStream(data)) {

            Thumbnails.of(is).scale(scale).outputQuality(quality).toOutputStream(os);
            return os.toByteArray();
        }
    }
}
