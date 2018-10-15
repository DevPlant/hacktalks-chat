package org.hacktalks.chat;

import com.mongodb.MongoClientOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableAsync
@SpringBootApplication
public class ChatApplication {

    @Bean
    public MongoClientOptions mongoOptions() {
        return MongoClientOptions.builder().threadsAllowedToBlockForConnectionMultiplier(2).maxConnectionIdleTime(1)
                .connectionsPerHost(1).minConnectionsPerHost(1).socketTimeout(2000).build();
    }

    @Bean(name = "taskExecutor")
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(100);
        taskExecutor.setQueueCapacity(10000);
        return taskExecutor;
    }

    @Bean
    protected WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                configurer.setTaskExecutor(taskExecutor());
            }
        };
    }

    @Bean
    CommandLineRunner cmd(MongoOperations mongoOperations) {
        return args -> {
            if (!mongoOperations.collectionExists("messages")) {
                mongoOperations.createCollection("messages",
                        CollectionOptions.empty().maxDocuments(1000000L).size(1000000L).capped());
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
}


