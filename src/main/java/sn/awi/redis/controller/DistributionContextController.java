package sn.awi.redis.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;

import sn.awi.redis.service.DistributionContextService;
import sn.awi.redis.service.FileProcessingService;

@RequestMapping("/api")
@RestController
public class DistributionContextController {
	
	@Autowired
	private FileProcessingService fileProcessingService;
	
	@Autowired
	private DistributionContextService distributionContextService;
	
	@GetMapping("/bykey")
	public DistributionContext getDistributionContextByKey(@RequestParam("key") String key){
		return distributionContextService.getDistributionContextByKey(key);
	}

	@GetMapping("/all")
	public List<DistributionContext> getAllDistributionContext(){
		return distributionContextService.getAllDistributionContext();
	}

	@GetMapping("/update")
	public void updateDcxOnRedis(){
		System.out.println("Start of DCXs loading");
    	fileProcessingService.unzipDcxFiles();
    	System.out.println("End of DCXs loading");
	}

}
