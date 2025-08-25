package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonConsumer;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonAdresse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDetaljer;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.repository.OrganisasjonBestillingProgressRepository;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import no.nav.dolly.service.excel.dto.ExceldataOrdering;
import no.nav.dolly.service.excel.dto.OrganisasjonDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import static org.apache.poi.ss.usermodel.IgnoredErrorType.CALCULATED_COLUMN;
import static org.apache.poi.ss.usermodel.IgnoredErrorType.EMPTY_CELL_REFERENCE;
import static org.apache.poi.ss.usermodel.IgnoredErrorType.EVALUATION_ERROR;
import static org.apache.poi.ss.usermodel.IgnoredErrorType.FORMULA;
import static org.apache.poi.ss.usermodel.IgnoredErrorType.FORMULA_RANGE;
import static org.apache.poi.ss.usermodel.IgnoredErrorType.LIST_DATA_VALIDATION;
import static org.apache.poi.ss.usermodel.IgnoredErrorType.NUMBER_STORED_AS_TEXT;
import static org.apache.poi.ss.usermodel.IgnoredErrorType.TWO_DIGIT_TEXT_YEAR;
import static org.apache.poi.ss.usermodel.IgnoredErrorType.UNLOCKED_FORMULA;

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
    private static final int UNDERENHET = 14;

    private final KodeverkConsumer kodeverkConsumer;
    private final OrganisasjonBestillingProgressRepository organisasjonBestillingProgressRepository;
    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;
    private final OrganisasjonConsumer organisasjonConsumer;

    private static ExceldataOrdering unpackOrganisasjon(Integer posisjon, OrganisasjonDetaljer organisasjon,
                                                        Map<String, String> postnumre,
                                                        Map<String, String> landkoder) {

        return new ExceldataOrdering(organisasjon.getId(),
                getAlleEnheter(new ArrayList<>(), posisjon.toString(), organisasjon).stream()
                        .map(firma -> getFirma(firma, postnumre, landkoder))
                        .toList());
    }

    private static List<OrganisasjonDTO> getAlleEnheter(List<OrganisasjonDTO> organisasjoner,
                                                        String hierarki, OrganisasjonDetaljer organisasjon) {

        organisasjoner.add(new OrganisasjonDTO(hierarki, organisasjon));
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
        var siblingNo = Integer.parseInt(levels[levels.length - 1]);
        levels[levels.length - 1] = Integer.toString(++siblingNo);
        return StringUtils.join(levels, ".");
    }

    private static Object[] getFirma(OrganisasjonDTO firma, Map<String, String> postnumre, Map<String, String> landkoder) {

        return new Object[]{
                firma.hierarki(),
                nvl(firma.organisasjon().getOrganisasjonsnummer()),
                nvl(firma.organisasjon().getOrganisasjonsnavn()),
                nvl(firma.organisasjon().getEnhetstype()),
                nvl(firma.organisasjon().getStiftelsesdato()),
                nvl(firma.organisasjon().getNaeringskode()),
                nvl(firma.organisasjon().getSektorkode()),
                nvl(firma.organisasjon().getMaalform()),
                nvl(firma.organisasjon().getFormaal()),
                nvl(firma.organisasjon().getNettside()),
                nvl(firma.organisasjon().getTelefon()),
                nvl(firma.organisasjon().getEpost()),
                getForretningsadresse(firma.organisasjon().getAdresser(), postnumre, landkoder),
                getPostadresse(firma.organisasjon().getAdresser(), postnumre, landkoder),
                getUnderenheter(firma.organisasjon())
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

    public Mono<Void> prepareOrganisasjonSheet(XSSFWorkbook workbook, Long brukerId) {

        return getOrganisasjonsdetaljer(brukerId)
                .collectList()
                .map(rows -> {
                    var sheet = workbook.createSheet(ORGANISASJON_FANE);
                    sheet.addIgnoredErrors(new CellRangeAddress(0, rows.size(), 0, HEADER.length),
                            CALCULATED_COLUMN,
                            EMPTY_CELL_REFERENCE,
                            EVALUATION_ERROR,
                            FORMULA,
                            FORMULA_RANGE,
                            LIST_DATA_VALIDATION,
                            NUMBER_STORED_AS_TEXT,
                            TWO_DIGIT_TEXT_YEAR,
                            UNLOCKED_FORMULA);

                    var columnNo = new AtomicInteger(0);
                    Arrays.stream(COL_WIDTHS)
                            .forEach(colWidth -> sheet.setColumnWidth(columnNo.getAndIncrement(), colWidth * 256));

                    ExcelService.appendRows(workbook, ORGANISASJON_FANE,
                            Stream.of(Collections.singletonList(HEADER), rows)
                                    .flatMap(Collection::stream)
                                    .toList());
                    appendHyperlinkRelasjon(workbook, ORGANISASJON_FANE, rows, 1, UNDERENHET);
                    return sheet;
                })
                .then();
    }

    private Flux<Object[]> getOrganisasjonsdetaljer(Long brukerId) {

        var counter = new AtomicInteger(0);

        return organisasjonBestillingRepository.findByBrukerId(brukerId)
                .flatMap(orgBestilling ->
                    organisasjonBestillingProgressRepository.findByBestillingId(orgBestilling.getId()))
                .sort(Comparator.comparing(OrganisasjonBestillingProgress::getId).reversed())
                .map(OrganisasjonBestillingProgress::getOrganisasjonsnummer)
                .filter(orgnr -> !"NA".equals(orgnr))
                .distinct()
                .collectList()
                .flatMap(organisasjoner -> Mono.zip(
                        kodeverkConsumer.getKodeverkByName("Postnummer"),
                        kodeverkConsumer.getKodeverkByName("LandkoderISO2"),
                                Mono.just(organisasjoner)))
                .flatMapMany(tuple -> Flux.range(0, tuple.getT3().size() / FETCH_BLOCK_SIZE + 1)
                        .flatMap(index -> organisasjonConsumer.hentOrganisasjon(
                                tuple.getT3().subList(index * FETCH_BLOCK_SIZE,
                                        Math.min((index + 1) * FETCH_BLOCK_SIZE, tuple.getT3().size()))))
                        .sort(Comparator.comparing(OrganisasjonDetaljer::getId).reversed())
                        .map(organisasjon -> unpackOrganisasjon(counter.incrementAndGet(), organisasjon,
                                tuple.getT1(), tuple.getT2())))
                .sort(Comparator.comparing(ExceldataOrdering::organisasjonId).reversed())
                .map(ExceldataOrdering::exceldata)
                .flatMap(Flux::fromIterable);
    }
}
