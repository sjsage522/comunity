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

    @Bean
    CommandLineRunner initDatabase(UserService userService, CategoryService categoryService) {
        return new CommandLineRunner() {
            final Logger log = log();

            @Override
            public void run(final String... args) throws Exception {
                User newUser = User.createUser("Junseok1234", "junseok", "jun", "1234", "junseok@test.com");
                log.info("Preloading { userId : [" + userService
                        .join(
                                new UserJoinDto(
                                        newUser.getUserId(),
                                        newUser.getName(),
                                        newUser.getNickName(),
                                        newUser.getPassword(),
                                        newUser.getEmail()
                                )
                        ) + "] } CommandLineRunner...");

                Category newCategory = Category.createCategory("게임");
                log.info("Preloading { categoryName : [" + categoryService
                        .create(
                                new CategoryCreateDto(
                                        newCategory.getName()
                                )
                        ) + "] } CommandLineRunner...");
                Category newCategory2 = Category.createCategory("economy");
                log.info("Preloading { categoryName : [" + categoryService
                        .create(
                                new CategoryCreateDto(
                                        newCategory2.getName()
                                )
                        ) + "] } CommandLineRunner...");

                Logger logger = df.getBean(Logger.class);
                log.info("logger = " + logger);
            }
        };
    }
}
