package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.repository.IdentRepository;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class BankkontoExcelService {

    private static final Object[] header = {"Ident", "Kontonummer (Norge)", "Kontonummer (Utland)", "Banknavn", "Bankkode",
            "Banklandkode", "Valutakode", "Swift/Bickode", "Bankadresse1", "Bankadresse2", "Bankadresse3"};
    private static final Integer[] COL_WIDTHS = {14, 20, 20, 20, 20, 20, 20, 20, 30, 30, 30};
    private static final String ARK_FANE = "Bankkonti";
    private static final String LANDKODER = "Landkoder";
    private static final String VALUTAER = "Valutaer";
    private static final String KODEVERK_FMT = "%s (%s)";

    private final IdentRepository identRepository;
    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final KodeverkConsumer kodeverkConsumer;

    private static String getAdresse1(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && nonNull(bankkontonrUtland.getBankAdresse1()) ?
                bankkontonrUtland.getBankAdresse1() : "";
    }

    private static String getAdresse2(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && nonNull(bankkontonrUtland.getBankAdresse2()) ?
                bankkontonrUtland.getBankAdresse2() : "";
    }

    private static String getAdresse3(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && nonNull(bankkontonrUtland.getBankAdresse3()) ?
                bankkontonrUtland.getBankAdresse3() : "";
    }

    private static String getSwift(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && nonNull(bankkontonrUtland.getSwift()) ?
                bankkontonrUtland.getSwift() : "";
    }

    private static String getIban(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && nonNull(bankkontonrUtland.getIban()) ?
                bankkontonrUtland.getIban() : "";
    }

    private static String getBanknavn(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && nonNull(bankkontonrUtland.getBanknavn()) ?
                bankkontonrUtland.getBanknavn() : "";
    }

    private static String getBankkontonrNorge(BankkontonrNorskDTO bankkontonrNorsk) {

        return nonNull(bankkontonrNorsk) && nonNull(bankkontonrNorsk.getKontonummer()) ?
                bankkontonrNorsk.getKontonummer() : "";
    }

    private static String getBankkontonrUtland(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && nonNull(bankkontonrUtland.getKontonummer()) ?
                bankkontonrUtland.getKontonummer() : "";
    }

    private static String getLandkode(BankkontonrUtlandDTO bankkontonrUtland, Map<String, String> landkoder) {

        return nonNull(bankkontonrUtland) && nonNull(bankkontonrUtland.getLandkode()) ?
                String.format(KODEVERK_FMT, bankkontonrUtland.getLandkode(),
                        landkoder.getOrDefault(bankkontonrUtland.getLandkode(), bankkontonrUtland.getLandkode())) : "";
    }

    private static String getValuta(BankkontonrUtlandDTO bankkontonrUtland, Map<String, String> valutaer) {

        return nonNull(bankkontonrUtland) && nonNull(bankkontonrUtland.getValuta()) ?
                String.format(KODEVERK_FMT, bankkontonrUtland.getValuta(),
                        valutaer.getOrDefault(bankkontonrUtland.getValuta(), bankkontonrUtland.getValuta())) : "";
    }

    public void prepareBankkontoSheet(XSSFWorkbook workbook, XSSFCellStyle wrapStyle, List<String> testidenter) {

        var rows = getBankkontoDetaljer(testidenter);

        if (!rows.isEmpty()) {
            var sheet = workbook.createSheet(ARK_FANE);

            sheet.addIgnoredErrors(new CellRangeAddress(0, rows.size(), 0, header.length),
                    IgnoredErrorType.NUMBER_STORED_AS_TEXT);

            var columnNo = new AtomicInteger(0);
            Arrays.stream(COL_WIDTHS)
                    .forEach(colWidth -> sheet.setColumnWidth(columnNo.getAndIncrement(), colWidth * 256));

            ExcelService.appendRows(sheet, wrapStyle,
                    Stream.of(Collections.singletonList(header), rows)
                            .flatMap(Collection::stream)
                            .toList());
        }
    }

    private List<Object[]> getBankkontoDetaljer(List<String> identer) {

        var bankKontoIdenter = identRepository.findByIdentIn(identer)
                .stream()
                .map(Testident::getTestgruppe)
                .map(Testgruppe::getBestillinger)
                .flatMap(Collection::stream)
                .filter(bestilling -> bestilling.getBestKriterier().contains("Bankkonto"))
                .map(Bestilling::getProgresser)
                .flatMap(Collection::stream)
                .map(BestillingProgress::getIdent)
                .distinct()
                .toList();

        return Mono.zip(
                        kodeverkConsumer.getKodeverkByName(LANDKODER),
                        kodeverkConsumer.getKodeverkByName(VALUTAER))
                .flatMapMany(kodeverk -> Flux.range(0, bankKontoIdenter.size())
                        .flatMap(index -> tpsMessagingConsumer.getPersoner(List.of(bankKontoIdenter.get(index)), List.of("q1")))
                        .filter(PersonMiljoeDTO::isOk)
                        .map(PersonMiljoeDTO::getPerson)
                        .map(person -> unpackBankkonto(person, kodeverk)))
                .collectList()
                .block();
    }

    private Object[] unpackBankkonto(PersonDTO person, Tuple2 kodeverk) {

        return new Object[]{
                person.getIdent(),
                getBankkontonrNorge(person.getBankkontonrNorsk()),
                getBankkontonrUtland(person.getBankkontonrUtland()),
                getBanknavn(person.getBankkontonrUtland()),
                getIban(person.getBankkontonrUtland()),
                getLandkode(person.getBankkontonrUtland(), (Map<String, String>) kodeverk.getT1()),
                getValuta(person.getBankkontonrUtland(), (Map<String, String>) kodeverk.getT2()),
                getSwift(person.getBankkontonrUtland()),
                getAdresse1(person.getBankkontonrUtland()),
                getAdresse2(person.getBankkontonrUtland()),
                getAdresse3(person.getBankkontonrUtland())
        };
    }
}
