package com.trifork.idwsxua.fmktestclient;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.trifork.idwsxua.fmktestclient.util.Properties;

@SpringBootApplication
@Import(IDWSXUALibSpringConfiguration.class)
public class Application {

    private static String[] args;

    @Bean
    public static BeanPostProcessor cmdArgsBeanPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof Properties) {
                    try {
                        new CommandLineParser((Properties) bean).parse(args);
                    } catch (Exception e) {
                        throw new FatalBeanException("Problem parsing command line options", e);
                    }
                }
                return bean;
            }
        };
    }

    public static void main(String[] args) throws Exception {
        Application.args = args;
        SpringApplication.run(Application.class, args);
    }

}
