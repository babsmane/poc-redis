package sn.awi.redis.publication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sn.awi.redis.publication.exception.PublicationException;
import sn.awi.redis.publication.util.CompressionUtils;

@Service
public class CompressService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompressService.class);

    @Autowired
    private CompressionUtils compressionUtils;

    public String compress(String uncompressedJson) {
        try {
            byte[] compressedJsonBytes = compressionUtils.compressGzip(uncompressedJson.getBytes(StandardCharsets.UTF_8));
            return new String(compressedJsonBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("error compressing: " + e.getMessage(), e);
            throw new PublicationException(e.getMessage(), e);
        }
    }

    public String uncompress(String compressedJson) {
        try {
            byte[] uncompressedJsonBytes = compressionUtils.unCompressGzip(compressedJson.getBytes(StandardCharsets.UTF_8));
            return new String(uncompressedJsonBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("error uncompressing: " + e.getMessage(), e);
            throw new PublicationException(e.getMessage(), e);
        }
    }
}
