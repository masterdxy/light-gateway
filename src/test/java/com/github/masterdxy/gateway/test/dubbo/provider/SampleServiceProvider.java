package com.github.masterdxy.gateway.test.dubbo.provider;

import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

@Service(version = "1.0.0")
public class SampleServiceProvider implements ISampleService {

    @Override
    public String echo(String text) {
        return text;
    }

    @Override
    public EchoResponseDTO echo(EchoDTO echoDTO) {
        EchoResponseDTO responseDTO = new EchoResponseDTO();
        responseDTO.setName(echoDTO.getName());
        responseDTO.setAge(echoDTO.getAge());
        responseDTO.setSpeakEnglish(echoDTO.getSpeakEnglish());
        return responseDTO;
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SampleProviderConfig.class);
        context.start();
        System.in.read();
    }

    @Configuration
    @EnableDubbo(scanBasePackages = "com.github.masterdxy.gateway.test.dubbo.provider")
    @PropertySource("classpath:/spring/dubbo-provider.properties")
    static class SampleProviderConfig {
        @Bean
        public RegistryConfig registryConfig() {
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setAddress("nacos://127.0.0.1:8848");
            return registryConfig;
        }
    }
}
