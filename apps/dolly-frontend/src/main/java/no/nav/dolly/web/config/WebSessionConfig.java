package no.nav.dolly.web.config;

import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebSessionConfig {

    private final List<String> expiredSIDs = new ArrayList<>();

    public Mono<Void> addExpiredSID(String sid){
        return Mono.fromRunnable((() -> {
            if (sid != null && !expiredSIDs.contains(sid)) this.expiredSIDs.add(sid);
        }));
    }

    public Mono<Void> removeExpiredSID(String sid){
        return Mono.fromRunnable((() -> {
            if (sid!= null) this.expiredSIDs.remove(sid);
        }));
    }

    public List<String> getExpiredSIDs() {
        return expiredSIDs;
    }

}