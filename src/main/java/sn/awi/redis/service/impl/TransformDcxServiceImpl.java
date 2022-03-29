package sn.awi.redis.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allianz.emagin.eqs.engine.catalog.DistributionContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sn.awi.redis.publication.util.CompressionUtils;
import sn.awi.redis.service.TransformDcxService;

@Service
public class TransformDcxServiceImpl implements TransformDcxService {

    @Autowired
    private ObjectMapper processedObjMapper;

    @Autowired
    private CompressionUtils compressionUtils;

	@Override
	public byte[] transformByteFromDcx(DistributionContext distributionContext) {
		try {
			byte[] valueRaw = processedObjMapper.writeValueAsBytes(distributionContext);
            byte[] bytes = compressionUtils.compressGzip(valueRaw);
			return bytes;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public DistributionContext transformByteToDcx(byte[] json) {
		try {
            byte[] bytes = compressionUtils.unCompressGzip(json);
			return processedObjMapper.readValue(bytes, DistributionContext.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public DistributionContext transformStringToDcx(String json) {
		try {
			return processedObjMapper.readValue(json, DistributionContext.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
