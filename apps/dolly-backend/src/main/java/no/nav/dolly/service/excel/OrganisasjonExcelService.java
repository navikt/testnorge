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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonAdresse.AdresseType.FADR;
import static no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonAdresse.AdresseType.PADR;
import static no.nav.dolly.service.excel.ExcelUtil.appendHyperlinkRelasjon;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class OrganisasjonExcelService {

    public static final String ORGANISASJON_FANE = "Organisasjoner";
    private static final Object[] HEADER = {"Hierarki", "Organisasjonsnummer", "Organisasjonsnavn", "Enhetstype", "Stiftelsesdato",
            "Naeringskode", "Sektorkode", "Målform", "Formål", "Nettside", "Telefon", "Epost", "Forretningsadresse",
            "Postadresse", "Underenheter"};
    private static final Integer[] COL_WIDTHS = {10, 20, 25, 15, 15, 15, 15, 15, 30, 20, 20, 20, 30, 30, 15};
    private static final DateTimeFormatter NORSK_DATO = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final int FETCH_BLOCK_SIZE = 10;
    private static final int UNDERENHET = 13;

    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;
    private final OrganisasjonConsumer organisasjonConsumer;
    private final KodeverkConsumer kodeverkConsumer;

    private static List<Object[]> unpackOrganisasjon(Integer posisjon, OrganisasjonDetaljer organisasjon,
                                                     Map<String, String> postnumre,
                                                     Map<String, String> landkoder) {

        return getAlleEnheter(new HashMap<>(), posisjon.toString(), organisasjon)
                .entrySet().stream()
                .map(firma -> getFirma(firma, postnumre, landkoder))
                .toList();
    }

    private static Map<String, OrganisasjonDetaljer> getAlleEnheter(Map<String, OrganisasjonDetaljer> organisasjoner,
                                                             String hierarki, OrganisasjonDetaljer organisasjon) {

            organisasjoner.put(hierarki, organisasjon);
            if (!organisasjon.getUnderenheter().isEmpty()) {

                hierarki += ".0";
                for (OrganisasjonDetaljer underenhet : organisasjon.getUnderenheter()) {
                    hierarki = incrementAndGet(hierarki);
                    getAlleEnheter(organisasjoner, hierarki, underenhet);
                }
            }
            return organisasjoner;
    }

    private static String incrementAndGet(String hierarki) {

        var levels = hierarki.split("\\.");
        var siblingNo = Integer.parseInt(levels[levels.length-1]);
        levels[levels.length-1] = Integer.toString(++siblingNo);
        return StringUtils.join(levels, ".");
    }

    private static Object[] getFirma(Map.Entry <String, OrganisasjonDetaljer> organisasjon, Map<String, String> postnumre, Map<String, String> landkoder) {

        return new Object[]{
                organisasjon.getKey(),
                nvl(organisasjon.getValue().getOrganisasjonsnummer()),
                nvl(organisasjon.getValue().getOrganisasjonsnavn()),
                nvl(organisasjon.getValue().getEnhetstype()),
                nvl(organisasjon.getValue().getStiftelsesdato()),
                nvl(organisasjon.getValue().getNaeringskode()),
                nvl(organisasjon.getValue().getSektorkode()),
                nvl(organisasjon.getValue().getMaalform()),
                nvl(organisasjon.getValue().getFormaal()),
                nvl(organisasjon.getValue().getNettside()),
                nvl(organisasjon.getValue().getTelefon()),
                nvl(organisasjon.getValue().getEpost()),
                getForretningsadresse(organisasjon.getValue().getAdresser(), postnumre, landkoder),
                getPostadresse(organisasjon.getValue().getAdresser(), postnumre, landkoder),
                getUnderenheter(organisasjon.getValue())
        };
    }

    private static Object getUnderenheter(OrganisasjonDetaljer organisasjon) {

        return organisasjon.getUnderenheter().stream()
                .map(OrganisasjonDetaljer::getOrganisasjonsnummer)
                .collect(Collectors.joining(",\n"));
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

        if (nonNull(rows) && !rows.isEmpty()) {
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

            appendHyperlinkRelasjon(workbook, ORGANISASJON_FANE, rows, UNDERENHET);
        }
    }

    private List<Object[]> getOrganisasjonsdetaljer(Bruker bruker) {

        var organisasjoner = organisasjonBestillingRepository.findByBruker(bruker).stream()
                .map(OrganisasjonBestilling::getProgresser)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(OrganisasjonBestillingProgress::getId).reversed())
                .map(OrganisasjonBestillingProgress::getOrganisasjonsnummer)
                .filter(orgnr -> !"NA".equals(orgnr))
                .distinct()
                .toList();

        var counter = new AtomicInteger(0);
        return Mono.zip(kodeverkConsumer.getKodeverkByName("Postnummer"),
                        kodeverkConsumer.getKodeverkByName("LandkoderISO2"))
                .flatMapMany(kodeverk -> Flux.range(0, organisasjoner.size() / FETCH_BLOCK_SIZE + 1)
                        .flatMap(index -> organisasjonConsumer.hentOrganisasjon(
                                organisasjoner.subList(index * FETCH_BLOCK_SIZE,
                                        Math.min((index + 1) * FETCH_BLOCK_SIZE, organisasjoner.size()))
                        ))
                        .map(organisasjon -> unpackOrganisasjon(counter.incrementAndGet(), organisasjon,
                                kodeverk.getT1(), kodeverk.getT2())))
                .flatMap(Flux::fromIterable)
                .collectList()
                .block();

    }
}
