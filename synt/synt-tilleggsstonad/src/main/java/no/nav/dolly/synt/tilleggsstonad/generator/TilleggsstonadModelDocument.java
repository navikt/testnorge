package no.nav.dolly.synt.tilleggsstonad.generator;

import no.nav.dolly.libs.syntbean.BeanModelDocument;

import java.util.List;
import java.util.Map;

record TilleggsstonadModelDocument(
        int formatVersion,
        String model,
        String root,
        List<String> deltaColumns,
        Map<String, Map<String, BeanModelDocument>> beanModels,
        Map<String, BeanModelDocument.KdeDocument> kde
) {
}
