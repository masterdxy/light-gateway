package com.github.masterdxy.gateway.verticle;

public class ManagerVerticle {

    //ManagerVerticle  manage cluster of gateway.
    //Init hazelcast for response cache and ratelimiter,sub redis some channel, and push manage event,like endpoint changes.
    //Handle ThreadDump and HeadDump req

    //1.Init cluster with hazelcast.
    //2.Load endpoint config and others with distributed lock from remote db. (jdbc)
    //3.Register manager endpoint e.g. /thread_dump and listen. (web)
    //4.Watch config changes by period retrieve remote db, send msg through event bus.(timer)
}
