package sn.awi.redis.catalog.loader;

import java.math.BigDecimal;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;
import com.allianz.emagin.eqs.engine.catalog.PremiumApplyToType;
import com.allianz.emagin.eqs.engine.catalog.PremiumRoundType;
import com.allianz.emagin.eqs.engine.catalog.PremiumStrategy;
import com.allianz.emagin.eqs.engine.catalog.PremiumType;

import sn.awi.redis.dto.DistributionContextDTO;

public class DcxDefinitionLoaderPremiumStrategy {

    private DcxDefinitionLoaderPremiumStrategy() {}

    public static void loadPremiumStrategy(DistributionContext dcx, DistributionContextDTO beanDTO) {
        PremiumType premiumType = PremiumType.GROSS;
        if (beanDTO.getPremiumType()!=null) {
            premiumType = PremiumType.valueOf(beanDTO.getPremiumType());
        }
        dcx.setPremiumType(premiumType);

        if (beanDTO.getPricingStrategy()!=null) {
            PremiumStrategy premiumStrategy = new PremiumStrategy();
            premiumStrategy.setId(beanDTO.getPricingStrategy().getId());
            if (beanDTO.getPricingStrategy().getAppliedTo() != null) {
                premiumStrategy.setAppliedTo(PremiumApplyToType.valueOf(beanDTO.getPricingStrategy().getAppliedTo()));
            }
            if (beanDTO.getPricingStrategy().getRoundingMethod()!=null) {
                premiumStrategy.setRoundingMethod(PremiumRoundType.valueOf(beanDTO.getPricingStrategy().getRoundingMethod()));
            }
            premiumStrategy.setRoundedTo(beanDTO.getPricingStrategy().getRoundedTo());
            premiumStrategy.setPlusMinusValue(beanDTO.getPricingStrategy().getPlusMinusValue());
            DcxDefinitionLoaderPremiumStrategy.completeIfNullStrategyValues(premiumStrategy);
            dcx.setPremiumStrategy(premiumStrategy);
        }
    }

    private static void completeIfNullStrategyValues(PremiumStrategy premiumStrategy) {
        if(premiumStrategy.getRoundedTo() == null) {
            premiumStrategy.setRoundedTo(new BigDecimal("0.01"));
        }
        if(premiumStrategy.getRoundingMethod() == null) {
            premiumStrategy.setRoundingMethod(PremiumRoundType.STANDARD);
        }
        if(premiumStrategy.getPlusMinusValue() == null) {
            premiumStrategy.setPlusMinusValue(BigDecimal.ZERO);
        }
    }


}
