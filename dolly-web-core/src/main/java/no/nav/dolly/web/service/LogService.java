package no.nav.dolly.web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import no.nav.dolly.web.domain.LogEvent;

@Slf4j
@Service
public class LogService {
    public void log(LogEvent event){
        switch (event.getLevel()){
            case INFO:
                log.info(event.toString());
                break;
            case WARING:
                log.warn(event.toString());
                break;
            case ERROR:
                log.error(event.toString());
                break;
            default:
                log.debug(event.toString());
                break;
        }
    }
}