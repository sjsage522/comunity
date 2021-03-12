package com.example.comunity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import static org.springframework.hateoas.config.EnableHypermediaSupport.*;

/**
 * affordance ->  to take this,
 * using a different hypermedia-based media type
 *
 * To switch to HAL-FORMS, create this codes.
 */
@Configuration
@EnableHypermediaSupport(type = HypermediaType.HAL_FORMS)
public class HypermediaConfiguration {
}
