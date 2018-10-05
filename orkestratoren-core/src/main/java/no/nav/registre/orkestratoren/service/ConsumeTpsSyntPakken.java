package no.nav.registre.orkestratoren.service;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConsumeTpsSyntPakken {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    public void produserOgSendSkdmeldingerTilTpsIMiljoer(List<String> miljoer, int antall_personer) {

    }
}
