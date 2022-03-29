package sn.awi.redis.catalog.loader;

import java.io.File;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allianz.emagin.eqs.engine.catalog.DisplayCurrency;
import com.allianz.emagin.eqs.engine.catalog.DistributionContext;
import com.allianz.emagin.eqs.engine.catalog.group.Group;
import com.allianz.emagin.eqs.engine.catalog.pricing.CatalogDiscount;
import com.allianz.emagin.eqs.engine.catalog.properties.BaseProperty;
import com.allianz.emagin.eqs.engine.catalog.repositories.Country;
import com.allianz.emagin.eqs.engine.catalog.repositories.SalesChannel;
import com.allianz.emagin.eqs.engine.catalog.repositories.Touchpoint;
import com.allianz.emagin.eqs.engine.catalog.segmentation.Segment;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sn.awi.redis.catalog.exception.CatalogLoaderException;
import sn.awi.redis.dto.CategoryDTO;
import sn.awi.redis.dto.CategoryTranslateDTO;
import sn.awi.redis.dto.DiscountDTO;
import sn.awi.redis.dto.DistributionContextDTO;

public class DcxDefinitionLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DcxDefinitionLoader.class);

    private DcxDefinitionLoader() {}

    public static DistributionContext loadDCXDefinition(final CatalogLoaderFiles catalogLoaderFiles, final File file, final CatalogCommonData catalogCommonData, final ObjectMapper mapper,
                                                        final Map<String, Group> groups, final Map<String, Segment> segments) {
        final String jsonString = catalogLoaderFiles.loadFileContent(file);
        DistributionContextDTO beanDTO = null;
        try {
            beanDTO = mapper.readValue(jsonString, DistributionContextDTO.class);
        } catch (final Exception e) {
            LOGGER.error("Loading dcx file : " + file.getName(), e);
            throw new CatalogLoaderException("Loading dcx file : " + file.getName(), e);
        }
        // Instanciante DCX Definition
        final DistributionContext dcx = new DistributionContext();
        dcx.setUuid(beanDTO.getId().toString());
        dcx.setPartner(beanDTO.getPartner().getCode());
        dcx.setActivated(beanDTO.isActivated());
        for (int i = 0; i < beanDTO.getCountries().length; i++) {
            final Country country = catalogCommonData.getAllCountries().get(beanDTO.getCountries()[i] + "");
            if (country == null) {
                LOGGER.debug(String.format("Country is null: %d", beanDTO.getId()));
            }
            else {
                dcx.getCountries().add(country.getCode());
            }
        }
        for (int i = 0; i < beanDTO.getSalesChannels().length; i++) {
            final SalesChannel salesChannel = catalogCommonData.getAllSalesChanels().get(beanDTO.getSalesChannels()[i] + "");
            if (salesChannel == null) {
                LOGGER.debug(String.format("Sales Channels is null: %d", beanDTO.getId()));
            }
            else {
                dcx.getSalesChanels().add(salesChannel.getCode());
            }
        }
        for (int i = 0; i < beanDTO.getTouchPoints().length; i++) {
            final Touchpoint touchpoint = catalogCommonData.getAllTouchPoints().get(beanDTO.getTouchPoints()[i] + "");
            if (touchpoint == null) {
                LOGGER.debug(String.format("Touch point is null: %d", beanDTO.getId()));
            }
            else {
                dcx.getTouchpoints().add(touchpoint.getCode());
            }
        }
        if (beanDTO.getDomain() != null && !beanDTO.getDomain().isEmpty()) {
            dcx.setDomain(beanDTO.getDomain());
        }
        DcxDefinitionLoader.loadLanguages(beanDTO, dcx, catalogCommonData);
        DcxDefinitionLoader.loadCurrencies(beanDTO, dcx, catalogCommonData);
        // Load properties
        try {
            final ObjectMapper mapper1 = new ObjectMapper();
            final JsonNode actualObj = mapper1.readTree(jsonString);
            final JsonNode jsonNode1 = actualObj.get(CatalogLoaderConstants.JSON_CONSTANT_PROPERTIES);
            final Iterator<JsonNode> elms = jsonNode1.elements();
            while (elms.hasNext()) {
                final BaseProperty bp = catalogCommonData.getPropertyHelper().buildProperty(elms.next());
                if (bp == null) {
                    CatalogLoaderCounters.incrementNbBasePropertyNullDCX();
                } else {
                	if(bp.isMarketingProperty())
                    dcx.getMarkertingProperties().add(bp);
                	else
                    dcx.getProperties().add(bp);
                    dcx.getPropDefProperty().put(bp.getDefinitionId(), bp);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Loading dcx id : " + beanDTO.getId().toString(), e);
            throw new CatalogLoaderException("Loading dcx id : " + beanDTO.getId().toString(), e);
        }
        dcx.constructClientId();
        // load categories
        DcxDefinitionLoader.loadCategories(dcx, beanDTO, catalogCommonData, groups);
        // load segments
        DcxDefinitionLoaderSegment.loadSegment(dcx, beanDTO, groups, segments, catalogCommonData);
        if (beanDTO.getDefaultSegment()!=null && segments.containsKey(beanDTO.getDefaultSegment().longValue()+"")) {
            dcx.setDefaultSegment(segments.get(beanDTO.getDefaultSegment().longValue() + ""));
        }
        // load discounts
        DcxDefinitionLoader.loadDiscount(dcx, beanDTO);
        // PREMIUM STRATEGY
        DcxDefinitionLoaderPremiumStrategy.loadPremiumStrategy(dcx, beanDTO);
        return dcx;
    }

    private static void loadLanguages(final DistributionContextDTO beanDTO, final DistributionContext dcx,
                                      final CatalogCommonData catalogCommonData) {
        if (beanDTO.getMainLanguage() != 0) {
            CatalogLoaderRepositories.setMainLanguage(catalogCommonData.getAllLanguages().get(Integer.toString(beanDTO.getMainLanguage())), dcx);
        }

        if (beanDTO.getDefaultLanguage() != 0) {
            CatalogLoaderRepositories.setDefaultLanguage(catalogCommonData.getAllLanguages().get(Long.toString(beanDTO.getDefaultLanguage())),
                dcx);
        }

        List<String> otherLanguages = null;
        if (beanDTO.getLanguages().length > 0) {
            otherLanguages = Arrays.stream(beanDTO.getLanguages()).boxed()
                .map(val -> catalogCommonData.getAllLanguages().get(val.toString()).getCode()).collect(Collectors.toList());
        } else {
            otherLanguages = new ArrayList<>();
        }
        dcx.setOtherLanguages(otherLanguages);

    }

    private static void loadCurrencies(final DistributionContextDTO beanDTO, final DistributionContext dcx,
                                       final CatalogCommonData catalogCommonData) {
        if (beanDTO.getQuotationCurrency() != 0) {
            CatalogLoaderRepositories
                .setQuotationCurrency(catalogCommonData.getAllCurrencies().get(Integer.toString(beanDTO.getQuotationCurrency())), dcx);
        }

        Map<String, DisplayCurrency> displayCurrencies = null;
        if (!beanDTO.getDisplayCurrencies().isEmpty()) {
            displayCurrencies = beanDTO.getDisplayCurrencies().stream()
                .map(c -> {
                    Long id= c.getId();
                    BigDecimal cr= new BigDecimal(c.getConversionRate());
                    DisplayCurrency dc= new DisplayCurrency(id, cr);
                    String code = catalogCommonData.getAllCurrencies().get(c.getId().toString()).getCode();
                    String currencyCode = null;
                    if (code != null) {
                        currencyCode = code.toUpperCase();
                    }
                    Map.Entry<String, DisplayCurrency> res= new AbstractMap.SimpleEntry<>(currencyCode, dc);
                    return res;
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existingValue, newValue) -> newValue));
        } else {
            displayCurrencies = new HashMap<>();
        }
        dcx.setDisplayCurrencies(displayCurrencies);
    }

    private static void loadCategories(final DistributionContext dcx, final DistributionContextDTO beanDTO,
                                       final CatalogCommonData catalogCommonData, final Map<String, Group> groups) {
        if (beanDTO.getCategories() != null && !beanDTO.getCategories().isEmpty()) {
            for (final CategoryDTO cDto : beanDTO.getCategories()) {
                Group g = new Group();
                g.setId(cDto.getId());
                g.setCode(cDto.getName());
                g.setTitle(cDto.getTitle());
                g.setDescription(cDto.getDescription());
                g.setHighlightOfferId(cDto.getHighlightOffer());
                for (final Long id : cDto.getOffers()) {
                    g.getOffersId().add(id.longValue()+"");
                }
                g.setPriority(cDto.getPriority());
                if (cDto.getI18n() != null || !cDto.getI18n().isEmpty()) {
                    loadCategoriesTranslate(cDto, g, catalogCommonData);
                }

                g = dcx.group(g);
                if (g!=null) {
                    groups.put(g.getId().longValue() + "", g);
                }
            }
        }
    }

    private static void loadCategoriesTranslate(final CategoryDTO cDto, final Group g,
                                                final CatalogCommonData catalogCommonData) {
        for (final CategoryTranslateDTO categoryTranslateDTO : cDto.getI18n()) {
            g.translate(catalogCommonData.getAllLanguages().get(categoryTranslateDTO.getLanguageId().longValue() + "").getCode(),
                categoryTranslateDTO.getDisplayName(), categoryTranslateDTO.getDescription());
        }
    }

    private static void loadDiscount(final DistributionContext dcx, final DistributionContextDTO beanDTO) {
        if (beanDTO.getDiscounts() != null && !beanDTO.getDiscounts().isEmpty()) {
            for (final DiscountDTO dDto : beanDTO.getDiscounts()) {
                final CatalogDiscount catalogDiscount = new CatalogDiscount();
                catalogDiscount.setName(dDto.getName());
                catalogDiscount.setPromotionCode(dDto.getPromotionCode());
                catalogDiscount.setFlat(dDto.getFlat());

//                final DiscountType type;
//                if (dDto.getType() == null) {
//                    type = DiscountType.SUBSCRIPTION;
//                } else {
//                    type= DiscountType.valueOf(dDto.getType());
//                }
//                catalogDiscount.setType(type);
                catalogDiscount.setValue(dDto.getValue());
                catalogDiscount.setStartDate(dDto.getStartDate());
                catalogDiscount.setEndDate(dDto.getEndDate());
                for(final Long offerId : dDto.getOffers()) {
                    catalogDiscount.offer(offerId);
                }
                DcxDefinitionLoaderRule.createRule(dDto, catalogDiscount);
                dcx.discount(catalogDiscount);
            }
        }
    }

}
