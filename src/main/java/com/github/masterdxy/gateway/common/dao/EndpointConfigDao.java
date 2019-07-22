package com.github.masterdxy.gateway.common.dao;

import com.github.masterdxy.gateway.common.Endpoint;
import org.jfaster.mango.annotation.CacheBy;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.ReturnGeneratedId;
import org.jfaster.mango.annotation.SQL;

import java.util.List;

@DB(table = "gw_endpoint_config", name = "def_ds")
public interface EndpointConfigDao {


    @SQL("insert into #table (`uri`,`upstream_type`,`upstream_url`,`is_need_auth`,`is_need_sign`,`is_mock`) values " +
            "(:uri,:upstreamType,:upstreamUrl,:isNeedAuth,:isNeedSign,:isMock)")
    @ReturnGeneratedId
    Long create(Endpoint config);

    @SQL("update #table set uri=:uri,upstream_type=:upstreamType,upstream_url=:upstreamUrl,is_need_auth=:isNeedAuth," +
            "is_need_sign=:isNeedSign,is_mock=:isMock " + "where id=:id")
    Integer update(Endpoint config);

    @SQL("select * from #table where id = :1")
    Endpoint findOne(@CacheBy Long id);

    @SQL("delete from #table where id=:1")
    Integer delete(@CacheBy Long id);


    @SQL("select * from #table")
    List<Endpoint> findAll();
}
