package com.example.comunity.config;

import com.example.comunity.domain.Category;
import com.example.comunity.domain.User;
import com.example.comunity.dto.category.CategoryCreateDto;
import com.example.comunity.dto.user.UserJoinDto;
import com.example.comunity.service.CategoryService;
import com.example.comunity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class LoadDatabase {
    private final DefaultListableBeanFactory df;

    @Bean
    public Logger log() {
        return LoggerFactory.getLogger(LoadDatabase.class);
    }

    /**
     * Use Spring to inject {@link UserService} and {@link CategoryService} that can then load data. Since this will run
     * only after the app is operational, the database will be up.
     *
     * @param userService .
     * @param categoryService .
     */
    @Bean
    CommandLineRunner initDatabase(UserService userService, CategoryService categoryService) {
        return new CommandLineRunner() {
            final Logger log = log();

            @Override
            public void run(final String... args) throws Exception {
                Category newCategory = Category.from("게임");
                log.info("Preloading { categoryName : [" + categoryService
                        .create(
                                new CategoryCreateDto(
                                        newCategory.getCategoryName()
                                )
                        ) + "] } CommandLineRunner...");
                Category newCategory2 = Category.from("economy");
                log.info("Preloading { categoryName : [" + categoryService
                        .create(
                                new CategoryCreateDto(
                                        newCategory2.getCategoryName()
                                )
                        ) + "] } CommandLineRunner...");

                Logger logger = df.getBean(Logger.class);
                log.info("logger = " + logger);
            }
        };
    }
}
