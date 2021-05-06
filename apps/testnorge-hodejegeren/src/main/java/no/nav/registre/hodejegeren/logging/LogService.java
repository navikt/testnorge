package no.nav.registre.hodejegeren.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogService {

    public void log(LogEvent event) {
        MDC.clear();
        MDC.setContextMap(event.toPropertyMap());
        switch (event.getLevel()) {
        case INFO:
            log.info(event.getMessage());
            break;
        case WARNING:
            log.warn(event.getMessage());
            break;
        case ERROR:
            log.error(event.getMessage());
            break;
        default:
            log.debug(event.getMessage());
            break;
        }
        MDC.clear();
    }
}