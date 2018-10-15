package org.hacktalks.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

@RequestMapping("/api")
@RestController
class ChatCtrl {

    @Autowired
    private MessageRepo messageRepo;

    @GetMapping("/test")
    public String test() {
        return "Test";
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> messages() {
        return messageRepo.findAllByMessageTimeGreaterThanEqual(System.currentTimeMillis()).doOnTerminate(()->{
            System.out.println("Terminate ... ");
        });
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
