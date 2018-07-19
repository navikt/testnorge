package no.nav.service;

import no.nav.dolly.repository.BestillingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BestillingService {

    @Autowired
    private BestillingRepository bestillingRepository;

}
