package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonConsumer;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonAdresse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDetaljer;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonAdresse.AdresseType.FADR;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonAdresse.AdresseType.PADR;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class OrganisasjonExcelService {

    public static final String ORGANISASJON_FANE = "Organisasjoner";
    private static final Object[] HEADER = {"Organisasjonsnummer", "Organisasjonsnavn", "Enhetstype", "Stiftelsesdato",
            "Naeringskode", "Sektorkode", "Målform", "Formål", "Nettside", "Telefon", "Epost", "Forretningsadresse",
            "Postadresse"};
    private static final Integer[] COL_WIDTHS = {20, 25, 15, 15, 15, 15, 15, 30, 20, 20, 20, 30, 30};
    private static final DateTimeFormatter NORSK_DATO = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final int FETCH_BLOCK_SIZE = 10;

    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;
    private final OrganisasjonConsumer organisasjonConsumer;
    private final KodeverkConsumer kodeverkConsumer;

    private static Object[] unpackOrganisasjon(OrganisasjonDetaljer organisasjon,
                                               Map<String, String> postnumre,
                                               Map<String, String> landkoder) {

        return new Object[]{
                nvl(organisasjon.getOrganisasjonsnummer()),
                nvl(organisasjon.getOrganisasjonsnavn()),
                nvl(organisasjon.getEnhetstype()),
                nvl(organisasjon.getStiftelsesdato()),
                nvl(organisasjon.getNaeringskode()),
                nvl(organisasjon.getSektorkode()),
                nvl(organisasjon.getMaalform()),
                nvl(organisasjon.getFormaal()),
                nvl(organisasjon.getNettside()),
                nvl(organisasjon.getTelefon()),
                nvl(organisasjon.getEpost()),
                getForretningsadresse(organisasjon.getAdresser(), postnumre, landkoder),
                getPostadresse(organisasjon.getAdresser(), postnumre, landkoder)
        };
    }

    private static String nvl(String value) {

        return isNotBlank(value) ? value : "";
    }

    private static String nvl(LocalDate value) {

        return nonNull(value) ? value.format(NORSK_DATO) : "";
    }

    private static Object getPostadresse(List<OrganisasjonAdresse> adresser,
                                         Map<String, String> postnumre,
                                         Map<String, String> landkoder) {

        return getAdresse(adresser, PADR, postnumre, landkoder);
    }

    private static String getForretningsadresse(List<OrganisasjonAdresse> adresser,
                                                Map<String, String> postnumre,
                                                Map<String, String> landkoder) {

        return getAdresse(adresser, FADR, postnumre, landkoder);
    }

    private static String getAdresse(List<OrganisasjonAdresse> adresser,
                                     OrganisasjonAdresse.AdresseType type,
                                     Map<String, String> postnumre,
                                     Map<String, String> landkoder) {

        return adresser.stream()
                .filter(adresse -> type == adresse.getAdressetype())
                .map(adresse -> new StringBuilder()
                        .append(StringUtils.join(adresse.getAdresselinjer(), ", "))
                        .append(", ")
                        .append(adresse.getPostnr())
                        .append(' ')
                        .append("NO".equals(adresse.getLandkode()) ? postnumre.get(adresse.getPostnr()) : adresse.getPoststed())
                        .append(", ")
                        .append(landkoder.get(adresse.getLandkode()))
                        .toString())
                .findFirst().orElse("");
    }

    public void prepareOrganisasjonSheet(XSSFWorkbook workbook, Bruker bruker) {

        var rows = getOrganisasjonsdetaljer(bruker);

        if (!rows.isEmpty()) {
            var sheet = workbook.createSheet(ORGANISASJON_FANE);

            sheet.addIgnoredErrors(new CellRangeAddress(0, rows.size(), 0, HEADER.length),
                    IgnoredErrorType.NUMBER_STORED_AS_TEXT);

            var columnNo = new AtomicInteger(0);
            Arrays.stream(COL_WIDTHS)
                    .forEach(colWidth -> sheet.setColumnWidth(columnNo.getAndIncrement(), colWidth * 256));

            ExcelService.appendRows(workbook, ORGANISASJON_FANE,
                    Stream.of(Collections.singletonList(HEADER), rows)
                            .flatMap(Collection::stream)
                            .toList());
        }
    }

    private List<Object[]> getOrganisasjonsdetaljer(Bruker bruker) {

        var organisasjoner = organisasjonBestillingRepository.findByBruker(bruker).stream()
                .map(OrganisasjonBestilling::getProgresser)
                .flatMap(Collection::stream)
                .map(OrganisasjonBestillingProgress::getOrganisasjonsnummer)
                .filter(orgnr -> !"NA".equals(orgnr))
                .distinct()
                .toList();

        return Mono.zip(kodeverkConsumer.getKodeverkByName("Postnummer"),
                        kodeverkConsumer.getKodeverkByName("LandkoderISO2"))
                .flatMapMany(kodeverk -> Flux.range(0, organisasjoner.size() / FETCH_BLOCK_SIZE + 1)
                        .flatMap(index -> organisasjonConsumer.hentOrganisasjon(
                                organisasjoner.subList(index * FETCH_BLOCK_SIZE,
                                        Math.min((index + 1) * FETCH_BLOCK_SIZE, organisasjoner.size()))
                        ))
                        .map(organisasjon -> unpackOrganisasjon(organisasjon, kodeverk.getT1(), kodeverk.getT2())))
                .collectList()
                .block();

    }
}
