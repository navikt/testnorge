package no.nav.registre.orkestratoren.consumer.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import no.nav.registre.orkestratoren.config.exception.UnrecoverableException;

import no.rtv.namespacetps.TpsPersonDokumentType;

@Component
public class PersondokumentConverter {

    @Autowired
    private XmlMapper xmlMapper;

    public TpsPersonDokumentType convert(String message) {
        try {
            return xmlMapper.readValue(message, TpsPersonDokumentType.class);
        } catch (Exception e) {
            throw new UnrecoverableException("Feilet å konvertere tps persondokument", e);
        }
    }
}
