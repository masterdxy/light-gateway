package com.github.masterdxy.gateway.common.dao;

import com.github.masterdxy.gateway.common.MockData;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.crud.CrudDao;

import static com.github.masterdxy.gateway.common.Constant.DEFAULT_DATASOURCE_NAME;

@DB(table = "gw_endpoint_mock_data", name = DEFAULT_DATASOURCE_NAME)
public interface MockDataDao extends CrudDao<MockData,Long> {

}
