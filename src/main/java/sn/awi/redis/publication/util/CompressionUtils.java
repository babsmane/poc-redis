package sn.awi.redis.publication.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class CompressionUtils {

    @SuppressWarnings("deprecation")
	public byte[] compressGzip(byte[] value) throws IOException {
        ByteArrayOutputStream rstBao = new ByteArrayOutputStream();
        try (GZIPOutputStream zos = new GZIPOutputStream(rstBao,true)) {
            zos.write(value);
            zos.flush();
            rstBao.flush();
            IOUtils.closeQuietly(zos);
        }
        byte[] compressedBytes = rstBao.toByteArray();
        IOUtils.closeQuietly(rstBao);
        return Base64.encodeBase64(compressedBytes);
    }

    public byte[] unCompressGzip(byte[] compressedBytes) throws IOException {
        byte[] decodedBytes = Base64.decodeBase64(compressedBytes);

        try (ByteArrayInputStream bin = new ByteArrayInputStream(decodedBytes);
            GZIPInputStream gzipper = new GZIPInputStream(bin))
        {
            // Not sure where to go here
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int len;
            while ((len = gzipper.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            gzipper.close();
            out.close();
            return out.toByteArray();
        }
    }

}