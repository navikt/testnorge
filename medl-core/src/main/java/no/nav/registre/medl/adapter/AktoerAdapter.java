package no.nav.registre.medl.adapter;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.medl.consumer.rs.response.MedlSyntResponse;
import no.nav.registre.medl.database.model.TAktoer;
import no.nav.registre.medl.database.repository.AktoerRepository;

import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AktoerAdapter {

    private static final Long PARTITION_SIZE = 1000L;
    private final AktoerRepository aktoerRepository;

    public AktoerAdapter(AktoerRepository aktoerRepository) {
        this.aktoerRepository = aktoerRepository;
    }

    public List<String> filtrerIdenter(List<String> identer) {
        return partitionList(identer, PARTITION_SIZE)
                .stream()
                .map(list -> Lists.newArrayList(aktoerRepository.findAllByIdentIn(list)))
                .flatMap(Collection::stream)
                .map(TAktoer::getIdent)
                .collect(Collectors.toList());
    }

    public List<String> filtererAktoerIder(List<Long> aktoerIder) {
        return partitionList(aktoerIder, PARTITION_SIZE)
                .stream()
                .map(list -> Lists.newArrayList(aktoerRepository.findAllById(list)))
                .flatMap(Collection::stream)
                .map(TAktoer::getIdent)
                .collect(Collectors.toList());
    }

    public TAktoer opprettAktoer(MedlSyntResponse data, String aktoerId, String fnr) {
        return aktoerRepository.save(TAktoer.builder()
                .aktoerid(aktoerId)
                .datoEndret(data.getDatoEndret().isBlank() ? null : createTimestampFromDate((data.getDatoEndret())))
                .datoOpprettet(data.getDatoOpprettet().isBlank() ? null : createTimestampFromDate(data.getDatoOpprettet()))
                .ident(fnr)
                .sistSynkronisert(new Date(createTimestampFromDate(data.getDatoEndret()).getTime()))
                .build());
    }

    private <T> Collection<List<T>> partitionList(List<T> list, long partitionSize) {
        AtomicInteger counter = new AtomicInteger(0);
        return list
                .stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / partitionSize))
                .values();
    }

    private Timestamp createTimestampFromDate(
            String partialDate
    ) {
        java.util.Date date = new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        Date parseDateFormat = parseDateFormat(partialDate);
        return Timestamp.valueOf(parseDateFormat + " " + dateFormat.format(date));
    }

    private Date parseDateFormat(
            String date
    ) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-dd-MM");
        DateFormat normalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date s = dateFormat.parse(date);
            String s2 = normalDateFormat.format(s);
            return Date.valueOf(s2);
        } catch (ParseException e) {
            log.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
