package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class OrgnummerConsumerService {

    public List<String> getOrgnummer() {
        return Collections.emptyList();
    }
}
