package no.nav.registre.testnorge.miljoerservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MiljoerService {

    @Value("${config.miljoer}")
    private List<String> miljoer;

    public List<String> getMiljoer() {

        return miljoer;
    }
}
