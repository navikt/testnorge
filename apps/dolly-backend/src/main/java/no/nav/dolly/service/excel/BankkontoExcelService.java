package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.kontoregisterservice.KontoregisterConsumer;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.repository.IdentRepository;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.KontoDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.service.excel.ExcelUtil.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class BankkontoExcelService {

    private final static LocalDateTime NY_KONTOREGISTER_I_BRUK = LocalDateTime.of(2022, 8, 30, 0, 0);

    private final IdentRepository identRepository;
    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final KontoregisterConsumer kontoregisterConsumer;

    private static String getAdresse1(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getBankadresse1()) ?
                konto.getUtenlandskKontoInfo().getBankadresse1() : "";
    }

    private static String getAdresse1(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getBankAdresse1()) ?
                bankkontonrUtland.getBankAdresse1().trim() : "";
    }

    private static String getAdresse2(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getBankadresse2()) ?
                konto.getUtenlandskKontoInfo().getBankadresse2() : "";
    }

    private static String getAdresse2(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getBankAdresse2()) ?
                bankkontonrUtland.getBankAdresse2().trim() : "";
    }

    private static String getAdresse3(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getBankadresse3()) ?
                konto.getUtenlandskKontoInfo().getBankadresse3() : "";
    }

    private static String getAdresse3(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getBankAdresse3()) ?
                bankkontonrUtland.getBankAdresse3().trim() : "";
    }

    private static String getSwift(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getSwiftBicKode()) ?
                konto.getUtenlandskKontoInfo().getSwiftBicKode() : "";
    }

    private static String getSwift(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getSwift()) ?
                bankkontonrUtland.getSwift().trim() : "";
    }

    private static String getIban(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getKontonummer()) ?
                konto.getKontonummer() : "";
    }

    private static String getIban(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getIban()) ?
                bankkontonrUtland.getIban().trim() : "";
    }

    private static String getBanknavn(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getBanknavn()) ?
                konto.getUtenlandskKontoInfo().getBanknavn() : "";
    }

    private static String getBanknavn(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getBanknavn()) ?
                bankkontonrUtland.getBanknavn().trim() : "";
    }

    private static String getBankkontonrNorge(KontoDTO konto) {

        return nonNull(konto) && isNull(konto.getUtenlandskKontoInfo()) ?
                konto.getKontonummer() : "";
    }

    private static String getBankkontonrNorge(BankkontonrNorskDTO bankkontonrNorsk) {

        return nonNull(bankkontonrNorsk) && isNotBlank(bankkontonrNorsk.getKontonummer()) ?
                bankkontonrNorsk.getKontonummer().replace(".", "") : "";
    }

    private static String getBankkontonrUtland(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) ?
                konto.getKontonummer() : "";
    }

    private static String getBankkontonrUtland(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getKontonummer()) ?
                bankkontonrUtland.getKontonummer().trim() : "";
    }

    private static String getLandkode(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getBankLandkode()) ?
                konto.getUtenlandskKontoInfo().getBankLandkode() : "";
    }

    private static String getLandkode(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getLandkode()) ?
                bankkontonrUtland.getLandkode() : "";
    }

    private static String getValuta(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getValutakode()) ?
                konto.getUtenlandskKontoInfo().getValutakode() : "";
    }

    private static String getValuta(BankkontonrUtlandDTO bankkontonrUtland) {

        return nonNull(bankkontonrUtland) && isNotBlank(bankkontonrUtland.getValuta()) ?
                bankkontonrUtland.getValuta() : "";
    }

    public Mono<Void> prepareBankkontoSheet(XSSFWorkbook workbook, Testgruppe testgruppe) {

        var rows = getBankkontoDetaljer(testgruppe);

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

    private Mono<List<Object[]>> tpsBankkonto(List<String> identer) {
        return Flux.range(0, identer.size())
                .flatMap(index -> tpsMessagingConsumer.getPersoner(List.of(identer.get(index)), List.of("q1")))
                .filter(PersonMiljoeDTO::isOk)
                .map(PersonMiljoeDTO::getPerson)
                .map(person -> unpackBankkonto(person))
                .collectList();
    }

    private Mono<List<Object[]>> kontoregisterBankkonto(List<String> identer) {
        return Flux.range(0, identer.size())
                .flatMap(index -> kontoregisterConsumer.sendHentKontoRequest(identer.get(index)))
                .filter(konto -> konto != null && konto.getAktivKonto() != null)
                .map(konto -> unpackBankkonto(konto.getAktivKonto()))
                .collectList();
    }

    private List<Object[]> getBankkontoDetaljer(Testgruppe testgruppe) {

        var bankKontoIdenter = testgruppe.getBestillinger().stream()
                .filter(bestilling -> nonNull(bestilling.getBestKriterier()))
                .filter(bestilling -> bestilling.getBestKriterier().contains("Bankkonto"))
                .map(Bestilling::getProgresser)
                .flatMap(Collection::stream)
                .collect(Collectors.teeing(
                        Collectors.filtering(
                                p -> !p.getBestilling().getSistOppdatert().isAfter(NY_KONTOREGISTER_I_BRUK),
                                Collectors.toList()
                        ),
                        Collectors.filtering(
                                p -> p.getBestilling().getSistOppdatert().isAfter(NY_KONTOREGISTER_I_BRUK)
                                        && !p.getKontoregisterStatus().contains("Feil"),
                                Collectors.toList()
                        ),
                        List::of
                ))
                .stream()
                .map(list -> list.stream()
                        .map(BestillingProgress::getIdent)
                        .distinct()
                        .collect(Collectors.toList())
                )
                .toList();

        return List.of(
                        tpsBankkonto(bankKontoIdenter.get(0)),
                        kontoregisterBankkonto(bankKontoIdenter.get(1))
                )
                .parallelStream()
                .map(m -> m.block())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
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

    private Object[] unpackBankkonto(KontoDTO konto) {

        return new Object[]{
                konto.getKontohaver(),
                getBankkontonrNorge(konto),
                getBankkontonrUtland(konto),
                getBanknavn(konto),
                getIban(konto),
                getLandkode(konto),
                getValuta(konto),
                getSwift(konto),
                getAdresse1(konto),
                getAdresse2(konto),
                getAdresse3(konto)
        };
    }
}
