package no.nav.testnav.apps.syntvedtakshistorikkservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.PdlProxyConsumer;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Tags;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagsService {
    public static final List<Tags> SYNT_TAGS = List.of(Tags.ARENASYNT);

    private final PdlProxyConsumer pdlProxyConsumer;

    public boolean opprettetTagsPaaIdenter(List<String> identer) {
        return pdlProxyConsumer.createTags(identer, SYNT_TAGS);
    }

    public void removeTagsPaaIdent(String ident) {
        pdlProxyConsumer.deleteTags(Collections.singletonList(ident), SYNT_TAGS);
    }
}
