package com.github.masterdxy.gateway.config;

import org.jfaster.mango.datasource.DataSourceFactory;
import org.jfaster.mango.datasource.SimpleDataSourceFactory;
import org.jfaster.mango.operator.Mango;
import org.jfaster.mango.operator.cache.LocalCacheHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static com.github.masterdxy.gateway.common.Constant.DEFAULT_DATASOURCE_NAME;

@Configuration
public class MangoConfig {
	
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public Mango createMango () {
		Mango instance = Mango.newInstance();
		instance.setCacheHandler(new LocalCacheHandler());
		DataSourceFactory dsf = new SimpleDataSourceFactory(DEFAULT_DATASOURCE_NAME, dataSource);
		instance.setDataSourceFactory(dsf);
		instance.setLazyInit(false);
		return instance;
	}
	
}
