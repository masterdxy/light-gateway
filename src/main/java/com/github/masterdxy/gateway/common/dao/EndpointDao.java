package com.github.masterdxy.gateway.common.dao;

import com.github.masterdxy.gateway.common.Endpoint;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.crud.CrudDao;

import static com.github.masterdxy.gateway.common.Constant.DEFAULT_DATASOURCE_NAME;

@DB(table = "gw_endpoint_config", name = DEFAULT_DATASOURCE_NAME)
public interface EndpointDao extends CrudDao<Endpoint,Long> {

}
