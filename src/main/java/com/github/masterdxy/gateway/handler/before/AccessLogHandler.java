package com.github.masterdxy.gateway.handler.before;

import com.github.masterdxy.gateway.protocol.Protocol;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpVersion;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tomoyo
 */
@Component
@Lazy(value = false)
public class AccessLogHandler implements Handler<RoutingContext> {
	
	private static final Logger           logger                   = LoggerFactory.getLogger(AccessLogHandler.class);
	private static       SimpleDateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm:SSS");
	private static       String           format                   =
		"--> Remote IP :{}, Request URI :{}, Protocol Version :{}";
	
	/**
	 * Local log Kafka log TODO support json path for params select. TODO support
	 *
	 * @param context
	 */
	@Override
	public void handle (RoutingContext context) {
		logger.info(format,
		            context.request().host(),
		            context.request().absoluteURI(),
		            context.request().getHeader(Protocol.VERSION_HEADER)
		           );
		
		// common logging data
		long timestamp = System.currentTimeMillis();
		String remoteClient = context.request().remoteAddress().host();
		HttpMethod method = context.request().method();
		String uri = context.request().uri();
		HttpVersion version = context.request().version();
		
		log(context, timestamp, remoteClient, version, method, uri);
		context.next();
	}
	
	private void log (RoutingContext context, long timestamp, String remoteClient, HttpVersion version,
		HttpMethod method, String uri) {
		HttpServerRequest request = context.request();
		long contentLength = 0;
		Object obj = request.headers().get("content-length");
		if (obj != null) {
			try {
				contentLength = Long.parseLong(obj.toString());
			}
			catch (NumberFormatException ignore) {
				// ignore it and continue
			}
		}
		String versionFormatted = "-";
		switch (version) {
			case HTTP_1_0:
				versionFormatted = "HTTP/1.0";
				break;
			case HTTP_1_1:
				versionFormatted = "HTTP/1.1";
				break;
			case HTTP_2:
				versionFormatted = "HTTP/2.0";
				break;
			default: {
				versionFormatted = "UNKNOWN/0.0";
				break;
			}
		}
		
		final MultiMap headers = request.headers();
		String message;
		
		// as per RFC1945 the header is referer but it is not mandatory some implementations use referrer
		String referrer = headers.contains("referrer") ? headers.get("referrer") : headers.get("referer");
		String userAgent = request.headers().get("user-agent");
		referrer = referrer == null ? "-" : referrer;
		userAgent = userAgent == null ? "-" : userAgent;
		
		message = String.format("%s - - [%s] \"%s %s %s\" %d \"%s\" \"%s\"",
		                        remoteClient,
		                        DEFAULT_DATE_TIME_FORMAT.format(new Date(timestamp)),
		                        method,
		                        uri,
		                        versionFormatted,
		                        contentLength,
		                        referrer,
		                        userAgent
		                       );
		logger.info(message);
	}
	
}
