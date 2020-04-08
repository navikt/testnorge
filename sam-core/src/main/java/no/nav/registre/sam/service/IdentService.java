package no.nav.registre.sam.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sam.database.TPersonRepository;
import no.nav.registre.sam.domain.database.TPerson;

@Service
@RequiredArgsConstructor
public class IdentService {

    private static final int PAGE_SIZE = 500;

    private final TPersonRepository tPersonRepository;

    public List<String> filtrerIdenter(List<String> identer) {
        List<String> identerISam = new ArrayList<>();
        var paginerteIdenter = paginerIdenter(identer);
        for (var identerPage : paginerteIdenter) {
            var personer = tPersonRepository.findAllByFnrFKIn(identerPage);
            identerISam.addAll(personer.stream().map(TPerson::getFnrFK).collect(Collectors.toList()));
        }
        return identerISam;
    }

    private static List<List<String>> paginerIdenter(List<String> list) {
        int size = list.size();
        int m = size / PAGE_SIZE;
        if (size % PAGE_SIZE != 0) {
            m++;
        }

        List<List<String>> partisjoner = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            int fromIndex = i * PAGE_SIZE;
            int toIndex = i * PAGE_SIZE + PAGE_SIZE < size ? i * PAGE_SIZE + PAGE_SIZE : size;

            partisjoner.add(new ArrayList<>(list.subList(fromIndex, toIndex)));
        }
        return partisjoner;
    }
}
