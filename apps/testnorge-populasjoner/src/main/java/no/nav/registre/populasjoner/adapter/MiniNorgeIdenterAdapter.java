package no.nav.registre.populasjoner.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.Set;

import no.nav.registre.populasjoner.consumer.HodejegerenConsumer;

@Controller
@RequiredArgsConstructor
public class MiniNorgeIdenterAdapter implements IdenterAdapter {

    private final HodejegerenConsumer hodejegerenConsumer;

    @Override
    public Set<String> getIdenter() {
        return hodejegerenConsumer.getAllIdenter();
    }
}
