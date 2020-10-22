package no.nav.registre.testnorge.organisasjon.domain;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.eregmapper.v1.EregMapperDTO;
import no.nav.registre.testnorge.libs.dto.eregmapper.v1.NavnDTO;
import no.nav.registre.testnorge.organisasjon.consumer.dto.OrganisasjonDTO;


@Slf4j
@Value
public class Organisasjon {
    String orgnummer;
    String enhetType;
    String navn;
    String juridiskEnhet;
    Adresse postadresse;
    Adresse forretningsadresser;
    String redigertnavn;
    List<String> driverVirksomheter;

    public Organisasjon(OrganisasjonDTO dto) {

        navn = dto.getNavn().getNavnelinje1();
        orgnummer = dto.getOrganisasjonsnummer();
        juridiskEnhet = dto.getParents().isEmpty() ? null : dto.getParents().get(0).getOrganisasjonsnummer();
        redigertnavn = dto.getNavn().getRedigertnavn();
        enhetType = dto.getDetaljer().getEnhetstype();

        log.info("Driver antall virksomheter: {}", dto.getChildren() != null ? dto.getChildren().size() : "[ingen]");


        dto.getChildren().forEach(value -> log.info(value.toString()));

        driverVirksomheter = dto.getChildren() != null
                ? dto.getChildren().stream().map(OrganisasjonDTO::getOrganisasjonsnummer).collect(Collectors.toList())
                : Collections.emptyList();

        if (dto.getOrganisasjonDetaljer() != null) {
            var postadresser = dto.getOrganisasjonDetaljer().getPostadresser();
            if (postadresser != null && !postadresser.isEmpty()) {
                this.postadresse = new Adresse(postadresser.get(0));
            } else {
                this.postadresse = null;
            }
            var forretningsadresser = dto.getOrganisasjonDetaljer().getForretningsadresser();
            if (forretningsadresser != null && !forretningsadresser.isEmpty()) {
                this.forretningsadresser = new Adresse(forretningsadresser.get(0));
            } else {
                this.forretningsadresser = null;
            }
        } else {
            this.postadresse = null;
            this.forretningsadresser = null;
        }
    }

    public Organisasjon(no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO dto) {
        orgnummer = dto.getOrgnummer();
        enhetType = dto.getEnhetType();
        navn = dto.getNavn();
        juridiskEnhet = dto.getJuridiskEnhet();
        if (dto.getPostadresse() != null) {
            postadresse = new Adresse(dto.getPostadresse());
        } else {
            postadresse = null;
        }
        if (dto.getForretningsadresser() != null) {
            forretningsadresser = new Adresse(dto.getForretningsadresser());
        } else {
            forretningsadresser = null;
        }
        redigertnavn = dto.getRedigertnavn();
        driverVirksomheter = dto.getDriverVirksomheter();
    }

    public no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO toDTO() {
        return no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO.builder()
                .navn(navn)
                .enhetType(enhetType)
                .orgnummer(orgnummer)
                .juridiskEnhet(juridiskEnhet)
                .postadresse(postadresse != null ? postadresse.toDTO() : null)
                .forretningsadresser(forretningsadresser != null ? forretningsadresser.toDTO() : null)
                .redigertnavn(redigertnavn)
                .build();
    }

    public EregMapperDTO toEregMapperDTO(boolean update) {
        var builder = EregMapperDTO.builder();

        if (Strings.isNotBlank(navn)) {
            builder.navn(NavnDTO.builder()
                    .redNavn(redigertnavn)
                    .navneListe(Collections.singletonList(navn))
                    .build()
            );
        }

        if (juridiskEnhet != null) {
            String type = new StringBuilder("    NSSY").replace(0, enhetType.length(), enhetType).toString();
            builder.knytninger(Collections.singletonList(new HashMap<>() {{
                put("orgnr", juridiskEnhet);
                put("type", type);
            }}));
        }

        return builder
                .endringsType(update ? "E" : "N")
                .enhetstype(enhetType)
                .orgnr(orgnummer)
                .forretningsAdresse(forretningsadresser != null ? forretningsadresser.toAdresseDTO() : null)
                .adresse(postadresse != null ? postadresse.toAdresseDTO() : null)
                .build();
    }
}
