package no.nav.registre.orkestratoren.consumer.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.nav.testnav.libs.domain.dto.namespacetps.TpsPersonDokumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.nav.registre.orkestratoren.config.exception.UnrecoverableException;

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
