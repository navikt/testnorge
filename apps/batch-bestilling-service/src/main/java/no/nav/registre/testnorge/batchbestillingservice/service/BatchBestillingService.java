package no.nav.registre.testnorge.batchbestillingservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchBestillingService {

    @Value("${config.miljoer}")
    private List<String> miljoer;

    public List<String> getMiljoer() {

        return miljoer;
    }
}
