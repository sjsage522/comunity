package com.example.comunity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import static org.springframework.hateoas.config.EnableHypermediaSupport.*;

@Configuration
@EnableHypermediaSupport(type = HypermediaType.HAL_FORMS)
public class HypermediaConfiguration {
}
