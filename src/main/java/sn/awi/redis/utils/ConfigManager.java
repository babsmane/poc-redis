package sn.awi.redis.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Configuration
@Data
public class ConfigManager {

    @Value("${qe.dcxmanager.origin.folder}")
    private String javaDcxOriginFolder;

    @Value("${emagin.quotation-engine-v3.environment}")
    private String environment;

    @Value("${emagin.quotation.engine.v3.subenvironment}")
    private String subEnvironment;

    @Value("${redis.key.target.app}")
    private String targetApp;

    @Value("${redis.key.target.table}")
    private String targetTable;

}
