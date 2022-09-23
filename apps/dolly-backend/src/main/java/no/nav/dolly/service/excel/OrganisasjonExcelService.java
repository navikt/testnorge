package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonConsumer;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDetaljer;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OrganisasjonExcelService {

    public static final String ORGANISASJON_FANE = "Organisasjoner";
    private static final Object[] HEADER = {"Organisasjonsnummer", "Organisasjonsnavn", "Enhetstype", "Naeringskode",
            "Sektorkode", "Formål", "Målform", "Stiftelsesdato", "Nettside", "Telefon", "Epost", "Forretningsadresse",
            "Postadresse"};
    private static final Integer[] COL_WIDTHS = {14, 10, 20, 20, 6, 8, 12, 12, 18, 20, 20, 25, 25, 25, 25, 14, 14, 14, 14, 14, 14};

    private static final int FETCH_BLOCK_SIZE = 10;

    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;
    private final OrganisasjonConsumer organisasjonConsumer;

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

        return Flux.range(0, organisasjoner.size() / FETCH_BLOCK_SIZE + 1)
                .flatMap(index -> organisasjonConsumer.hentOrganisasjon(
                        organisasjoner.subList(index * FETCH_BLOCK_SIZE,
                                Math.min((index + 1) * FETCH_BLOCK_SIZE, organisasjoner.size()))
                ))
                .map(organisasjon -> unpackOrganisasjon(organisasjon))
                .collectList()
                .block();
    }

    private Object[] unpackOrganisasjon(OrganisasjonDetaljer organisasjon) {

        return new Object[]{
                organisasjon.getOrganisasjonsnummer(),
                organisasjon.getOrganisasjonsnavn(),
                organisasjon.getEnhetstype(),
                organisasjon.getNaeringskode(),
                organisasjon.getSektorkode(),
                organisasjon.getFormaal(),
                organisasjon.getMaalform(),
                organisasjon.getStiftelsesdato(),
                organisasjon.getNettside(),
                organisasjon.getTelefon(),
                organisasjon.getEpost(),
                organisasjon.getAdresser(),
                organisasjon.getAdresser()
        };
    }
}
