package no.nav.registre.tss.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;

import no.nav.registre.tss.utils.Rutine990Util;

@Service
public class KodeverkService {

    @Autowired
    private JmsService jmsService;

    public Object hentKodeverk(String kodetabell, String brukerId, String miljoe) throws JMSException {
        String rutine990 = Rutine990Util.opprettRutine(kodetabell, brukerId);
        return jmsService.sendOgMotta990RutineFraTss(rutine990, jmsService.hentKoeNavnSamhandler(miljoe));
    }
}
