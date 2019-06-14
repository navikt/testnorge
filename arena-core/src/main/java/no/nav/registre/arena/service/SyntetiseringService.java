package no.nav.registre.arena.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.arena.consumer.rs.HodejegerenConsumer;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SyntetiseringService {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;



}
