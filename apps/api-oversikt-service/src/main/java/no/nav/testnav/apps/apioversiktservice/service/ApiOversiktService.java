package no.nav.testnav.apps.apioversiktservice.service;

import org.springframework.stereotype.Service;

@Service
public class ApiOversiktService {
    public void getDokumeter() {

        getClass().getClassLoader().getResourceAsStream("resources/data/apioversikt.csv");


    }
}
