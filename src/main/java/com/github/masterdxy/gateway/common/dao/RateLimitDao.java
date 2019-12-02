package com.github.masterdxy.gateway.common.dao;

import com.github.masterdxy.gateway.common.RateLimit;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.crud.CrudDao;

import static com.github.masterdxy.gateway.common.Constant.DEFAULT_DATASOURCE_NAME;

/**
 * @author tomoyo
 */
@DB(table = "gw_endpoint_rate_limit", name = DEFAULT_DATASOURCE_NAME) public interface RateLimitDao
    extends CrudDao<RateLimit, Long> {

}
