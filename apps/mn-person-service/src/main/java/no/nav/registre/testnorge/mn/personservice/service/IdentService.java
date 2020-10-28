package no.nav.registre.testnorge.mn.personservice.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class IdentService {
    Set<String> getIdenter() {
        //TODO Use pdl elastic search to find file
        return Set.of(
                "29099625137",
                "22099625698",
                "03068117630",
                "30039123645",
                "20039121582",
                "04028420728",
                "26129222874",
                "10089120697",
                "01038514438",
                "08069120467"
        );
    }
}
