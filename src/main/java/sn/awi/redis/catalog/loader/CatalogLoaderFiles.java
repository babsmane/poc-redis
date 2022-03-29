package sn.awi.redis.catalog.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sn.awi.redis.catalog.exception.CatalogLoaderException;

public class CatalogLoaderFiles {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogLoader.class);

    public File getFile(final String file) {
        return new File(file);
    }

    public String loadFileContent(final File jSonFile) {
        final StringBuilder stringBuffer = new StringBuilder();

        try {
            final InputStream resourceAsStream = new FileInputStream(jSonFile);
            final InputStreamReader ipsr = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8);
            final BufferedReader br = new BufferedReader(ipsr);
            String line;
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
            br.close();
        } catch (final Exception e) {
            LOGGER.error("load file " + jSonFile.getName(), e);
            throw new CatalogLoaderException("load file " + jSonFile.getName(), e);
        }
        return stringBuffer.toString();
    }

}
