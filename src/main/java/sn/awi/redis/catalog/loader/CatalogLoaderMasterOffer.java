package sn.awi.redis.catalog.loader;

import java.io.File;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allianz.emagin.eqs.engine.catalog.MasterOffer;
import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sn.awi.redis.catalog.exception.CatalogLoaderException;
import sn.awi.redis.dto.MasterOfferDTO;

public class CatalogLoaderMasterOffer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogLoader.class);

    private static int nbBasePropertyNullMO = 0;

    public MasterOffer loadMasterOffer(final CatalogLoaderFiles catalogLoaderFiles, final File file, final ObjectMapper mapper, final CatalogCommonData catalogCommonData) {
        final String jsonString = catalogLoaderFiles.loadFileContent(file);

        MasterOfferDTO beanDTO = null;
        try {
            beanDTO = mapper.readValue(jsonString, MasterOfferDTO.class);
        } catch (final Exception e) {
            LOGGER.error("Loading master offer file : " + file.getName(), e);
            throw new CatalogLoaderException("Loading master offer file : " + file.getName(), e);
        }
        final MasterOffer masterOffer = new MasterOffer();
        masterOffer.setUid(beanDTO.getId());
        masterOffer.setCode(beanDTO.getCode());
        masterOffer.setTitle(beanDTO.getName());
        masterOffer.setParentId(beanDTO.getParentId());
        masterOffer.setActivated(beanDTO.isActivated());
        // Load properties
        try {
            final ObjectMapper mapper1 = new ObjectMapper();
            final JsonNode actualObj = mapper1.readTree(jsonString);
            final JsonNode jsonNode1 = actualObj.get(CatalogLoaderConstants.JSON_CONSTANT_PROPERTIES);
            final Iterator<JsonNode> elms = jsonNode1.elements();
            while (elms.hasNext()) {
                final BaseProperty bp = catalogCommonData.getPropertyHelper().buildProperty(elms.next());
                if (bp == null) {
                    nbBasePropertyNullMO++;
                } else {
                    masterOffer.getProperties().add(bp);
                    masterOffer.getPropDefProperty().put(bp.getDefinitionId(), bp);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Loading master offer code : " + beanDTO.getCode(), e);
            throw new CatalogLoaderException("Loading master offer code : " + beanDTO.getCode(), e);
        }

        return masterOffer;
    }

}
