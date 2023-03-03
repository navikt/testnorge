package no.nav.pdl.forvalter.dto;

import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class OpprettRequestComparator implements Comparator<OpprettRequest> {
    @Override
    public int compare(OpprettRequest o1, OpprettRequest o2) {
        return Integer.compare(o1.getPerson().getAlias().size(), o2.getPerson().getAlias().size());
    }
}
