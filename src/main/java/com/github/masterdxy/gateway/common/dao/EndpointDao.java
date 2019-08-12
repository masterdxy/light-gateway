package com.github.masterdxy.gateway.common.dao;

import com.github.masterdxy.gateway.common.Endpoint;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.crud.CrudDao;

@DB(table = "gw_endpoint_config", name = "def_ds")
public interface EndpointDao extends CrudDao<Endpoint,Long> {

}
