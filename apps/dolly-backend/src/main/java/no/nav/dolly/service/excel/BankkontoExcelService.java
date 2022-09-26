package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.repository.IdentRepository;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.service.excel.ExcelUtil.BANKKONTO_COL_WIDTHS;
import static no.nav.dolly.service.excel.ExcelUtil.BANKKONTO_FANE;
import static no.nav.dolly.service.excel.ExcelUtil.BANKKONTO_HEADER;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class BankkontoExcelService {

    private final IdentRepository identRepository;
    private final TpsMessagingConsumer tpsMessagingConsumer;

    private static String getAdresse1(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getBankAdresse1()) ?
                bankkontonrUtland.getBankAdresse1().trim() : "";
    }

    private static String getAdresse2(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getBankAdresse2()) ?
                bankkontonrUtland.getBankAdresse2().trim() : "";
    }

    private static String getAdresse3(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getBankAdresse3()) ?
                bankkontonrUtland.getBankAdresse3().trim() : "";
    }

    private static String getSwift(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getSwift()) ?
                bankkontonrUtland.getSwift().trim() : "";
    }

    private static String getIban(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getIban()) ?
                bankkontonrUtland.getIban().trim() : "";
    }

    private static String getBanknavn(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getBanknavn()) ?
                bankkontonrUtland.getBanknavn().trim() : "";
    }

    private static String getBankkontonrNorge(BankkontonrNorskDTO bankkontonrNorsk) {

        return nonNull(bankkontonrNorsk) && isNotBlank(bankkontonrNorsk.getKontonummer()) ?
                bankkontonrNorsk.getKontonummer().replace(".", "") : "";
    }

    private static String getBankkontonrUtland(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getKontonummer()) ?
                bankkontonrUtland.getKontonummer().trim() : "";
    }

    private static String getLandkode(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getLandkode()) ?
                bankkontonrUtland.getLandkode() : "";
    }

    private static String getValuta(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getValuta()) ?
                bankkontonrUtland.getValuta() : "";
    }

    public Mono<Void> prepareBankkontoSheet(XSSFWorkbook workbook, List<String> testidenter) {

        var rows = getBankkontoDetaljer(testidenter);

        if (!rows.isEmpty()) {
            var sheet = workbook.createSheet(BANKKONTO_FANE);

            sheet.addIgnoredErrors(new CellRangeAddress(0, rows.size(), 0, BANKKONTO_HEADER.length),
                    IgnoredErrorType.NUMBER_STORED_AS_TEXT);

            var columnNo = new AtomicInteger(0);
            Arrays.stream(BANKKONTO_COL_WIDTHS)
                    .forEach(colWidth -> sheet.setColumnWidth(columnNo.getAndIncrement(), colWidth * 256));

            ExcelService.appendRows(workbook, BANKKONTO_FANE,
                    Stream.of(Collections.singletonList(BANKKONTO_HEADER), rows)
                            .flatMap(Collection::stream)
                            .toList());
        }

        return Mono.empty();
    }

    private List<Object[]> getBankkontoDetaljer(List<String> identer) {

        var bankKontoIdenter = identRepository.findByIdentIn(identer)
                .stream()
                .map(Testident::getTestgruppe)
                .map(Testgruppe::getBestillinger)
                .flatMap(Collection::stream)
                .filter(bestilling -> nonNull(bestilling.getBestKriterier()))
                .filter(bestilling -> bestilling.getBestKriterier().contains("Bankkonto"))
                .map(Bestilling::getProgresser)
                .flatMap(Collection::stream)
                .map(BestillingProgress::getIdent)
                .distinct()
                .toList();

        return Flux.range(0, bankKontoIdenter.size())
                        .flatMap(index -> tpsMessagingConsumer.getPersoner(List.of(bankKontoIdenter.get(index)), List.of("q1")))
                        .filter(PersonMiljoeDTO::isOk)
                        .map(PersonMiljoeDTO::getPerson)
                        .map(person -> unpackBankkonto(person))
                .collectList()
                .block();
    }

    private Object[] unpackBankkonto(PersonDTO person) {

        return new Object[]{
                person.getIdent(),
                getBankkontonrNorge(person.getBankkontonrNorsk()),
                getBankkontonrUtland(person.getBankkontonrUtland()),
                getBanknavn(person.getBankkontonrUtland()),
                getIban(person.getBankkontonrUtland()),
                getLandkode(person.getBankkontonrUtland()),
                getValuta(person.getBankkontonrUtland()),
                getSwift(person.getBankkontonrUtland()),
                getAdresse1(person.getBankkontonrUtland()),
                getAdresse2(person.getBankkontonrUtland()),
                getAdresse3(person.getBankkontonrUtland())
        };
    }
}
