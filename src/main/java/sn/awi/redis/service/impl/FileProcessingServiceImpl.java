package sn.awi.redis.service.impl;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;

import sn.awi.redis.catalog.loader.CatalogLoader;
import sn.awi.redis.repository.DistributionContextRepository;
import sn.awi.redis.service.FileProcessingService;
import sn.awi.redis.service.TransformDcxService;
import sn.awi.redis.utils.ConfigManager;
import sn.awi.redis.utils.Constants;
import sn.awi.redis.utils.FileUtils;

@Service
public class FileProcessingServiceImpl implements FileProcessingService {

	@Autowired
	private ConfigManager configManager;

	@Autowired
	private TransformDcxService transformDcxService;

	@Autowired
	private DistributionContextRepository distributionContextRepository;

	@Override
	public void unzipDcxFiles() {
		String path = configManager.getJavaDcxOriginFolder();
		String key = "";

		File repertoire = new File(path);
		File liste[] = repertoire.listFiles();

		if (liste != null) {
			File tmpFolder = new File(path + "/tmp");
			FileUtils.resetTemporaryDirectory(tmpFolder);
			for (int i = 0; i < liste.length; i++) {
				File file = liste[i];
				if (file.isFile() && FileUtils.isArchive(file)) {
					System.out.println("Processing file " + file.getName());
					try {
						FileUtils.unzipFolderZip4j(file.toPath(), tmpFolder.toPath());
					} catch (IOException e) {
						System.err.println("unable to process file" + file.getName());
					}
				}
			}
			File tmpFolderFiles[] = tmpFolder.listFiles();
			if (tmpFolderFiles != null) {
				for (int i = 0; i < tmpFolderFiles.length; i++) {
					File tmpSubFolder = tmpFolderFiles[i];
					CatalogLoader catalogLoader = new CatalogLoader();
					try{
					DistributionContext dcx = catalogLoader.loadOneDCX(path + "/tmp/"+tmpSubFolder.getName());
					//System.out.println(dcx.dump());
					byte[] json = transformDcxService.transformByteFromDcx(dcx);
					key = generateKey(dcx.getClients().get(0));
					distributionContextRepository.save(json, key);
					} catch(Exception e){
						System.out.println("DCX "+tmpSubFolder.getName()+" encountered an error and could not be loaded");
					}
				}
			}

			FileUtils.deleteDirectory(tmpFolder);
		} else {
			System.err.println("invalid folder or no file");
		}
		//retrieving all DCXs
		//System.out.println(transformDcxService.transformByteToDcx(distributionContextRepository.findById(key)).dump());
	}
	
	private String generateKey(String key){
		String redisPrefixKey = String.join(Constants.REDIS_KEYSPACE_SEP, configManager.getEnvironment(),
				configManager.getSubEnvironment(), configManager.getTargetApp(), configManager.getTargetTable());
	        return String.join(Constants.REDIS_KEYSPACE_SEP, redisPrefixKey, key);
	}
}
