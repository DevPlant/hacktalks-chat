package org.hacktalks.chat;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@SpringBootApplication
public class ChatApplication {

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

@RequestMapping("/api")
@RestController
class ChatCtrl {

    @Autowired
    private MessageRepo messageRepo;

    @GetMapping("/test")
    public String test() {
        return "Test";
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> messages() {
        return messageRepo.findAllByMessageTimeGreaterThanEqual(System.currentTimeMillis());
    }

    @GetMapping(value = "/chat/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Message> previousMessages(@RequestParam(value = "beforeTime", required = false) Long beforeTime) {
        if (beforeTime == null) {
            beforeTime = System.currentTimeMillis();
        }
        return messageRepo.findAllByMessageTimeLessThan(beforeTime,
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "messageTime")));
    }

    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Message> postMessage(@RequestBody Message message, Principal principal) {
        message.setMessageTime(System.currentTimeMillis());
        message.setFrom(principal != null ? principal.getName() : "Anonymous");
        return messageRepo.save(message);
    }

}

interface MessageRepo extends ReactiveMongoRepository<Message, String> {

    @Tailable
    Flux<Message> findAllByMessageTimeGreaterThanEqual(long messageTime);

    Flux<Message> findAllByMessageTimeLessThan(long messageTime, Pageable pageable);
}

@Data
@Document(collection = "messages")
class Message {

    @Id
    private String id;

    private String from;
    private String value;
    private long messageTime;
}