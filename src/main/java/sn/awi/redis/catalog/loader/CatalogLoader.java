package sn.awi.redis.catalog.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;
import com.allianz.emagin.eqs.engine.catalog.MasterOffer;
import com.allianz.emagin.eqs.engine.catalog.Offer;
import com.allianz.emagin.eqs.engine.catalog.group.Group;
import com.allianz.emagin.eqs.engine.catalog.pricing.CatalogDiscount;
import com.allianz.emagin.eqs.engine.catalog.properties.PropertyREF;
import com.allianz.emagin.eqs.engine.catalog.segmentation.Segment;
import com.carrotsearch.sizeof.RamUsageEstimator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sn.awi.redis.catalog.exception.CatalogLoaderException;
import sn.awi.redis.catalog.utils.PropertyHelperJsonRss;

public class CatalogLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogLoader.class);
    private final CatalogLoaderOffer calogLoaderOffer;
    private final CatalogLoaderMasterOffer catalogLoaderMasterOffer;
    private final Map<String, MasterOffer> products = new ConcurrentHashMap<String, MasterOffer>();
    private final Map<String, Offer> offers = new ConcurrentHashMap<String, Offer>();
    private final Map<String, Group> groups = new ConcurrentHashMap<String, Group>();
    private final Map<String, Segment> segments = new ConcurrentHashMap<String, Segment>();
    private final CatalogCommonData catalogCommonData = new CatalogCommonData();
    private final ObjectMapper mapper;
    private CatalogLoaderFiles catalogLoaderFiles;

    public CatalogLoader() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.catalogLoaderMasterOffer= new CatalogLoaderMasterOffer();
        this.calogLoaderOffer= new CatalogLoaderOffer();
        this.catalogLoaderFiles = new CatalogLoaderFiles();
    }

    public void setCatalogLoaderFiles(final CatalogLoaderFiles catalogLoaderFiles) {
        this.catalogLoaderFiles= catalogLoaderFiles;
    }
    public DistributionContext loadOneDCX(final String folder) {
        this.loadCommonData("/" + folder);
        final File directory = this.catalogLoaderFiles.getFile(folder);
        DistributionContext distributionContext = null;
        final File[] files = directory.listFiles();
        for (final File f : files) {
            if (f.isFile() && f.getName().startsWith(CatalogLoaderConstants.PUBLICATION_FILE_DCX_STARTER)) {
                distributionContext = DcxDefinitionLoader.loadDCXDefinition(this.catalogLoaderFiles, f, this.catalogCommonData, this.mapper, this.groups, this.segments);
                distributionContext.setPropDefCodeId(this.catalogCommonData.getPropDefCodeId());
                break;
            }
        }
        if (distributionContext == null) {
            throw new IllegalStateException("distribution context has not been loaded");
        }
        this.processPendingProperties();
        this.loadMetadata(folder, distributionContext);
        this.loadFiles(files, distributionContext);
        // Link Master Offer to offer
        if (distributionContext != null) {
            for (final MasterOffer mo : distributionContext.getMasterOffers()) {
                if (mo.getParentId() != null && !mo.getParentId().isEmpty()) {
                    mo.setParent(this.products.get(mo.getParentId()));
                } else {
                    mo.setParent(distributionContext);
                }
            }
            for (final Offer o : distributionContext.getOffers()) {
                o.setParent(this.products.get(o.getProductId()));
            }
        }
        // finalize categories
        this.finalizeCategories(distributionContext);
        // finalize discounts
        this.finalizeDiscounts(distributionContext);
        this.logLoadingResults(folder, distributionContext,
                this.catalogCommonData.getPropertyHelper().getPropertyHelperMaps().getPendindProperties());
        final long sizeDCX = RamUsageEstimator.sizeOf(distributionContext);
        LOGGER.debug("Size of distribution context "+sizeDCX);
        LOGGER.info("Distribution context for clients: " + distributionContext.getClients().toString());
        LOGGER.debug("Distribution context " + distributionContext.dump());
        LOGGER.debug("Size Distribution context " + sizeDCX);
        LOGGER.debug("Distribution context BP NULL : " + CatalogLoaderCounters.getNbBasePropertyNullDCX() +
            ", MO : "+ CatalogLoaderCounters.getNbBasePropertyNullMO() +
            ", O : "+ CatalogLoaderCounters.getNbBasePropertyNullO() );
        return distributionContext;
    }

    private void logLoadingResults(final String folder, final DistributionContext distributionContext, final List<PropertyREF> pendingProperties) {
        LOGGER.debug("-------------------------------- End Load dcx folder : " + folder + " | masteroffers : "
                + distributionContext.getMasterOffers().size() + " | offers : " + distributionContext.getOffers().size());
        if (!pendingProperties.isEmpty()) {
            for (final PropertyREF pRef : pendingProperties) {
                if (pRef.getPropertyDefinition() != null) {
                    LOGGER.debug("Pending Property " + pRef.getPropertyDefinition().getCode());

                } else {
                    LOGGER.debug("Pending Property <null>, id pending : " + pRef.getPropertyPendingId());
                }
            }
        }
    }

    private void loadFiles(final File[] files, final DistributionContext distributionContext) {
        for (final File f : files) {
            if (f.isFile() && f.getName().startsWith(CatalogLoaderConstants.PUBLICATION_FILE_MASTER_OFFER_STARTER)) {
                final MasterOffer masterOffer = this.catalogLoaderMasterOffer.loadMasterOffer(this.catalogLoaderFiles, f, this.mapper, this.catalogCommonData);
                this.products.put(masterOffer.getUid(), masterOffer);
                distributionContext.getMasterOffers().add(masterOffer);
                masterOffer.setDcx(distributionContext);
            }
        }
        this.processPendingProperties();
        for (final File f : files) {
            if (f.isFile() && f.getName().startsWith(CatalogLoaderConstants.PUBLICATION_FILE_OFFER_STARTER)) {
                final Offer offer = this.calogLoaderOffer.loadOffer(this.catalogLoaderFiles, f, this.mapper, this.catalogCommonData);
                distributionContext.getOffers().add(offer);
                offer.setDcx(distributionContext);
                this.offers.put(offer.getUid(), offer);
            }
        }
        this.processPendingProperties();
    }

    private void processPendingProperties() {
        final List<PropertyREF> toRemoves = new ArrayList<>();
        for (final PropertyREF bp : this.catalogCommonData.getPropertyHelper().getPropertyHelperMaps().getPendindProperties()) {
            if (this.catalogCommonData.getPropertyHelper().getPropertyHelperMaps().getProperties().containsKey(bp.getPropertyPendingId())) {
                bp.setProperty(this.catalogCommonData.getPropertyHelper().getPropertyHelperMaps().getProperties().get(bp.getPropertyPendingId()));
                toRemoves.add(bp);
            }
        }
        for (final PropertyREF pRef : toRemoves) {
            this.catalogCommonData.getPropertyHelper().getPropertyHelperMaps().getPendindProperties().remove(pRef);
        }
        PropertyHelperJsonRss.processPendingRelationShipRules(this.catalogCommonData.getPropertyHelper());
    }

    private void loadMetadata(final String folder, final DistributionContext dcx) {
        final File f = new File(folder + "/" + CatalogLoaderConstants.PUBLICATION_FILE_METADATA);
        if (!f.exists()) {
            return;
        }
        try {
            final String jsonString = this.catalogLoaderFiles.loadFileContent(f);
            if (jsonString != null && !jsonString.isEmpty()) {
                final JsonNode actualObj = this.mapper.readTree(jsonString);
                final JsonNode jsonNodePublicationId = actualObj.get(CatalogLoaderConstants.JSON_CONSTANT_MD_PUBLICATION_ID);
                if (jsonNodePublicationId != null && !jsonNodePublicationId.isNull()) {
                    dcx.setMdPublicationId(jsonNodePublicationId.asLong());
                }
                final JsonNode jsonNodeEnvironment = actualObj.get(CatalogLoaderConstants.JSON_CONSTANT_MD_ENVIRONMENT);
                if (jsonNodeEnvironment != null && !jsonNodeEnvironment.isNull()) {
                    dcx.setMdEnvironment(jsonNodeEnvironment.asText());
                }
                final JsonNode jsonNodeSubEnvironment = actualObj.get(CatalogLoaderConstants.JSON_CONSTANT_MD_SUB_ENVIRONMENT);
                if (jsonNodeSubEnvironment != null && !jsonNodeSubEnvironment.isNull()) {
                    dcx.setMdEnvironment(jsonNodeSubEnvironment.asText());
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Loading metadata file", e);
            throw new CatalogLoaderException("Loading metadata file", e);
        }
    }

    private void loadCommonData(final String folder) {
        try {
            final String jsonString = this.catalogLoaderFiles.loadFileContent(this.catalogLoaderFiles.getFile(folder + "/" + CatalogLoaderConstants.PUBLICATION_FILE_COMMONDATA));
            final JsonNode actualObj = this.mapper.readTree(jsonString);
            this.catalogCommonData.load(actualObj);
        } catch (final Exception e) {
            LOGGER.error("Loading common data", e);
            throw new CatalogLoaderException("Loading common data", e);
        }
    }

    private void finalizeCategories(final DistributionContext dcx) {
        if (dcx.getGroups() == null || dcx.getGroups().isEmpty()) {
            return;
        }
        for (final Group group : dcx.getGroups()) {
            if (group.getOffers() != null && !group.getOffers().isEmpty()) {
                continue;
            }
            for (final String offerId : group.getOffersId()) {
                group.offer(this.offers.get(offerId));
            }
            if (group.getHighlightOfferId() != null) {
                group.setHighlightOffer(this.offers.get(group.getHighlightOfferId().longValue() + ""));
            }
        }
    }

    private void finalizeDiscounts(final DistributionContext dcx) {
        if (dcx.getCatalogDiscounts() == null || dcx.getCatalogDiscounts().isEmpty()) {
            return;
        }
        for (final CatalogDiscount catalogDiscount : dcx.getCatalogDiscounts()) {
            if (catalogDiscount.getOffers() != null && !catalogDiscount.getOffers().isEmpty()) {
                continue;
            }
            for (final Long offerId : catalogDiscount.getOffers()) {
                catalogDiscount.offer(offerId);
            }
        }
    }

}
