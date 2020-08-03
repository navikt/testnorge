package no.nav.registre.testnorge.hendelse.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.hendelse.v1.HendelseType;
import no.nav.registre.testnorge.hendelse.domain.Hendelse;
import no.nav.registre.testnorge.hendelse.repository.HendelseRepository;
import no.nav.registre.testnorge.hendelse.repository.model.HendelseModel;


/**
 * Opptimalisert søk i databasen istede for å filtere i java service'en. Derfor er det mange kombinasjoner som nesten er like.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HendelseAdapter {
    private static final String HENDELSE_KEY = "Testnorge-Hendelse";
    private final HendelseRepository hendelseRepository;

    public void opprett(Hendelse hendelse) {
        MDC.put(HENDELSE_KEY, hendelse.getType().name());
        log.trace(hendelse.toString());
        hendelseRepository.save(hendelse.toHendelseModel());
    }

    public List<Hendelse> hentHendelser(HendelseType hendelse, String ident, LocalDate localDate) {
        if (localDate == null && ident == null && hendelse == null) {
            return hentHendelser();
        }
        if (localDate == null && ident == null) {
            return hentHendelser(hendelse);
        }
        if (localDate == null && hendelse == null) {
            return hentHendelser(ident);
        }
        if (ident == null && hendelse == null) {
            return hentHendelser(localDate);
        }

        if (localDate == null) {
            return hentHendelser(hendelse, ident);
        }
        if (hendelse == null) {
            return hentHendelser(ident, localDate);
        }
        if (ident == null) {
            return hentHendelser(hendelse, localDate);
        }
        return convert(hendelseRepository.findBy(ident, Date.valueOf(localDate), hendelse));
    }


    private List<Hendelse> hentHendelser(String ident, LocalDate localDate) {
        return convert(hendelseRepository.findBy(ident, Date.valueOf(localDate)));
    }

    private List<Hendelse> hentHendelser(HendelseType type) {
        return convert(hendelseRepository.findByHendelse(type));
    }

    private List<Hendelse> hentHendelser(HendelseType type, String ident) {
        return convert(hendelseRepository.findByIdentAndHendelse(ident, type));
    }

    private List<Hendelse> hentHendelser(HendelseType type, LocalDate localDate) {
        return convert(hendelseRepository.findBy(Date.valueOf(localDate), type));
    }

    private List<Hendelse> hentHendelser(String ident) {
        return convert(hendelseRepository.findByIdent(ident));
    }

    private List<Hendelse> hentHendelser(LocalDate localDate) {
        return convert(hendelseRepository.findBy(Date.valueOf(localDate)));
    }

    private List<Hendelse> hentHendelser() {
        return convert(hendelseRepository.findAll());
    }

    public List<Hendelse> convert(List<HendelseModel> models) {
        return models.stream().map(Hendelse::new).collect(Collectors.toList());
    }
}
