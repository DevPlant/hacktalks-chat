package org.hacktalks.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface MessageRepo extends ReactiveMongoRepository<Message, String> {

    @Tailable
    Flux<Message> findAllByMessageTimeGreaterThanEqual(long messageTime);

    Flux<Message> findAllByMessageTimeLessThan(long messageTime, Pageable pageable);
}

