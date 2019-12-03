package com.github.masterdxy.gateway.test.dubbo.provider;

public interface ISampleService {

    String echo(String text);

    EchoResponseDTO echo(EchoDTO echoDTO);



}
