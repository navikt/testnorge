package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.kontoregisterservice.KontoregisterConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.KontoDTO;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.service.excel.ExcelUtil.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankkontoExcelService {

    private final KontoregisterConsumer kontoregisterConsumer;

    private static String getAdresse1(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getBankadresse1()) ?
                konto.getUtenlandskKontoInfo().getBankadresse1() : "";
    }

    private static String getAdresse2(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getBankadresse2()) ?
                konto.getUtenlandskKontoInfo().getBankadresse2() : "";
    }

    private static String getAdresse3(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getBankadresse3()) ?
                konto.getUtenlandskKontoInfo().getBankadresse3() : "";
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

    private static String getBankkontonrNorge(KontoDTO konto) {

        return nonNull(konto) && isNull(konto.getUtenlandskKontoInfo()) ?
                konto.getKontonummer() : "";
    }

    private static String getBankkontonrUtland(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) ?
                konto.getKontonummer() : "";
    }

    private static String getLandkode(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getBankLandkode()) ?
                konto.getUtenlandskKontoInfo().getBankLandkode() : "";
    }

    private static String getValuta(KontoDTO konto) {

        return nonNull(konto) && nonNull(konto.getUtenlandskKontoInfo()) && isNotBlank(konto.getUtenlandskKontoInfo().getValutakode()) ?
                konto.getUtenlandskKontoInfo().getValutakode() : "";
    }

    public Mono<Void> prepareBankkontoSheet(XSSFWorkbook workbook, XSSFSheet sheet, Testgruppe testgruppe) {

        return getBankkontoDetaljer(testgruppe)
                .filter(rows -> !rows.isEmpty())
                .flatMap(rows -> {

                    sheet.addIgnoredErrors(new CellRangeAddress(0, rows.size(), 0, BANKKONTO_HEADER.length),
                            IgnoredErrorType.NUMBER_STORED_AS_TEXT);

                    var columnNo = new AtomicInteger(0);
                    Arrays.stream(BANKKONTO_COL_WIDTHS)
                            .forEach(colWidth -> sheet.setColumnWidth(columnNo.getAndIncrement(), colWidth * 256));

                    ExcelService.appendRows(workbook, BANKKONTO_FANE,
                            Stream.of(Collections.singletonList(BANKKONTO_HEADER), rows)
                                    .flatMap(Collection::stream)
                                    .toList());

                    return Mono.empty();
                });
    }

    private Mono<List<Object[]>> getBankkontoDetaljer(Testgruppe testgruppe) {

        var start = System.currentTimeMillis();
        return Flux.fromIterable(testgruppe.getBestillinger())
                .map(Bestilling::getProgresser)
                .flatMap(Flux::fromIterable)
                .filter(progress -> isNotBlank(progress.getKontoregisterStatus()))
                .map(BestillingProgress::getIdent)
                .distinct()
                .flatMap(ident -> kontoregisterConsumer.getKontonummer(ident)
                        .filter(response -> HttpStatus.OK.equals(response.getStatus()))
                        .map(response -> unpackBankkonto(response.getAktivKonto())))
                .collectList()
                .doOnNext(bankkonti -> log.info("Hentet {} antall bankkonti, tid {} secunder", bankkonti.size(),
                        (System.currentTimeMillis() - start)/1000));
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