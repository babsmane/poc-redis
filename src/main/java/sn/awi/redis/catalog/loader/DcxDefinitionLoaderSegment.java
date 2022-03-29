package sn.awi.redis.catalog.loader;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;
import com.allianz.emagin.eqs.engine.catalog.businessrules.Filter;
import com.allianz.emagin.eqs.engine.catalog.group.Group;
import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.allianz.emagin.eqs.engine.catalog.segmentation.DistributionVariant;
import com.allianz.emagin.eqs.engine.catalog.segmentation.DistributionVariantOffer;
import com.allianz.emagin.eqs.engine.catalog.segmentation.Segment;
import com.fasterxml.jackson.databind.JsonNode;

import sn.awi.redis.catalog.exception.CatalogLoaderException;
import sn.awi.redis.dto.DistributionContextDTO;
import sn.awi.redis.dto.DistributionVariantDTO;
import sn.awi.redis.dto.DistributionVariantOfferDTO;
import sn.awi.redis.dto.FilterDTO;
import sn.awi.redis.dto.SegmentDTO;

public class DcxDefinitionLoaderSegment {

    private static final Logger LOGGER = LoggerFactory.getLogger(DcxDefinitionLoaderSegment.class);

    private DcxDefinitionLoaderSegment() {}

    public static void loadSegment(final DistributionContext dcx, final DistributionContextDTO beanDTO,
                                   final Map<String, Group> groups, final Map<String, Segment> segments, final CatalogCommonData catalogCommonData) {
        if (beanDTO.getSegments() != null && !beanDTO.getSegments().isEmpty()) {
            for (final SegmentDTO sDto : beanDTO.getSegments()) {
                Segment s = new Segment();
                s.setId(sDto.getId());
                s.setCode(sDto.getCode());
                s.setTitle(sDto.getTitle());
                s.setPriority(sDto.getPriority());
                if (sDto.getFilters()!=null && !sDto.getFilters().isEmpty()) {
                    DcxDefinitionLoaderSegment.loadSegmentFilters(sDto, s);
                }
                if (sDto.getDvs() != null && !sDto.getDvs().isEmpty()) {
                    DcxDefinitionLoaderSegment.loadSegmentOptimize(sDto, groups, s, catalogCommonData);
                }
                s = dcx.segment(s);
                if (s!=null) {
                    segments.put(s.getId().longValue() + "", s);
                }
            }
        }
    }

    private static void loadSegmentOptimize(final SegmentDTO sDto,
                                            final Map<String, Group> groups, final Segment s, final CatalogCommonData catalogCommonData) {
        for (final DistributionVariantDTO dvDto : sDto.getDvs()) {
            final DistributionVariant dv = new DistributionVariant();
            dv.setId(dvDto.getId());
            dv.setCode(dvDto.getCode());
            dv.setTitle(dvDto.getTitle());
            dv.setTrafficAllocation(dvDto.getTrafficAllocation());
            dv.setCustomizeMarketingContent(dvDto.getCustomizeMarketingContent());
            if (dvDto.getGroup()!=null && groups.containsKey(dvDto.getGroup().longValue()+"")) {
                dv.setGroup(groups.get(dvDto.getGroup().longValue()+""));
            }
            s.optimize(dv);
            if (dv.getCustomizeMarketingContent()) {
                // Load properties
                try {
                    if (dvDto.getDisplayDcx()!=null && !dvDto.getDisplayDcx().isNull()) {
                        final JsonNode jsonNode1 = dvDto.getDisplayDcx().get(CatalogLoaderConstants.JSON_CONSTANT_PROPERTIES);
                        final Iterator<JsonNode> elms = jsonNode1.elements();
                        while (elms.hasNext()) {
                            final BaseProperty bp = catalogCommonData.getPropertyHelper().buildProperty(elms.next());
                            if (bp != null) {
                                dv.addProperty(bp);
                                dv.getDcxMarketingsCustomized().getPropDefProperty().put(bp.getDefinitionId(), bp);
                            }
                        }
                    }
                    
                    if (dvDto.getDisplays()!=null && !dvDto.getDisplays().isEmpty() ) {
                        for(final DistributionVariantOfferDTO dvoDto : dvDto.getDisplays()) {
                            final DistributionVariantOffer dvOffer = new DistributionVariantOffer();
                            dvOffer.setCode(dvoDto.getOfferId().toString());
                            dvOffer.setTitle(dvoDto.getName());
                            dvOffer.setDescription(dvoDto.getDescription());
                            final JsonNode jsonNode1 = dvoDto.getProperties();
                            final Iterator<JsonNode> elms = jsonNode1.elements();
                            while (elms.hasNext()) {
                                final BaseProperty bp = catalogCommonData.getPropertyHelper().buildProperty(elms.next());
                                if (bp != null) {
                                    dvOffer.addProperty(bp);
                                    dvOffer.getPropDefProperty().put(bp.getDefinitionId(), bp);
                                }
                            }
                            dv.offerDisplay(dvoDto.getOfferId().toString(), dvOffer);
                        }
                    }
                } catch (final Exception e) {
                    LOGGER.error("Loading DV id : " +dvDto.getId().toString(), e);
                    throw new CatalogLoaderException("Loading DV id : " + dvDto.getId().toString(), e);
                }
            }
        }
    }

    private static void loadSegmentFilters(final SegmentDTO sDto, final Segment s) {
        for(final FilterDTO fDto : sDto.getFilters()) {
            final Boolean hasDataToLoopOn= fDto.getDataToLoopOn() != null && !fDto.getDataToLoopOn().isEmpty();
            final Boolean hasSubRules= fDto.getSubRules() != null && !fDto.getSubRules().isEmpty();
            if(hasDataToLoopOn && !hasSubRules) {
                return;
            }
            final Filter segmentFilter = DcxDefinitionLoaderFilter.createFilter(fDto);
            if (hasSubRules) {
                for(final FilterDTO subFDTO : fDto.getSubRules()) {
                    final Filter subSegmentFilter = DcxDefinitionLoaderFilter.createFilter(subFDTO);
                    segmentFilter.subFilter(subSegmentFilter);
                }
            }
            s.filter(segmentFilter);
        }
    }
}