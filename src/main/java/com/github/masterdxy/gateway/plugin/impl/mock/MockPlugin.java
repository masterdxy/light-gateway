package com.github.masterdxy.gateway.plugin.impl.mock;

import com.github.masterdxy.gateway.common.Endpoint;
import com.github.masterdxy.gateway.common.MockData;
import com.github.masterdxy.gateway.plugin.Plugin;
import com.github.masterdxy.gateway.plugin.PluginChain;
import com.github.masterdxy.gateway.plugin.PluginResult;
import com.github.masterdxy.gateway.utils.ContextUtils;
import io.vertx.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.github.masterdxy.gateway.common.Constant.MOCK_DATA_KEY;

@Component
@Lazy(value = false)
public class MockPlugin implements Plugin {
	@Autowired
	private MockDataManager mockDataManager;
	
	@Override
	public int order () {
		return -99;
	}
	
	@Override
	public boolean match (RoutingContext context) {
		Endpoint endpoint = ContextUtils.getEndpoint(context);
		MockData mockData = mockDataManager.getMockDataByEpcId(endpoint.getId());
		if (mockData != null) {
			context.put(MOCK_DATA_KEY, mockData);
			return true;
		}
		return false;
	}
	
	@Override
	public PluginResult execute (RoutingContext context, PluginChain chain) {
		//Get mock data from storage.
		MockData mockData = context.get(MOCK_DATA_KEY);
		Objects.requireNonNull(mockData);
		return PluginResult.success(mockData.getMockData());
	}
}
