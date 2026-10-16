package no.nav.dolly.synt.meldekort.generator;

import no.nav.dolly.synt.meldekort.bean.BeanFixtures;
import no.nav.dolly.synt.meldekort.bean.BeanSynthesizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.SplittableRandom;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.within;

class MeldekortGeneratorTest {

    private static final String NS = "http://www.aetat.no/arena/mk_meldekort.xsd";
    private static final List<Integer> WEEKEND_DAYS = List.of(6, 7, 13, 14);

    @Test
    void shouldGenerateFourteenOrderedDaysFromModel()
            throws Exception {

        var generated = generator().generateMeldekort(MeldekortType.ARBS, 200, null, new SplittableRandom(7));

        assertThat(generated).hasSize(200);
        for (int index = 0; index < generated.size(); index++) {
            var meldekort = parse(generated.get(index));
            assertThat(meldekort.getAttribute("Id")).isEqualTo(String.valueOf(index));
            var dager = dager(meldekort);
            assertThat(dager).hasSize(14);
            for (int dag = 0; dag < dager.size(); dag++) {
                var element = dager.get(dag);
                var timer = verdi(element, "ArbeidetTimerSum");
                assertThat(text(element, "Dag")).isEqualTo(String.valueOf(dag + 1));
                assertThat(timer).isIn("0", "3.5", "7.5");
                assertThat(text(element, "Meldegruppe")).isEqualTo("ARBS");
                assertThat(verdi(element, "Syk")).isIn("true", "false");
                if (WEEKEND_DAYS.contains(dag + 1)) {
                    assertThat(timer).isEqualTo("0");
                }
            }
            assertThat(verdi(child(meldekort, "Signatur"), null)).isEqualTo("true");
        }

    }

    @Test
    void shouldFollowModelDistributionForHeadAnswers()
            throws Exception {

        var generated = generator().generateMeldekort(MeldekortType.ARBS, 4_000, null, new SplittableRandom(11));
        var expected = BeanFixtures.reference("ARBS").get("statistics").get("columns").get("ARBEIDET").get("frequencies").get("J").asDouble();

        var arbeidet = 0;
        for (var xml : generated) {
            var answer = child(parse(xml), "Arbeidet");
            var ja = verdi(child(answer, "SvarJa"), null);
            var nei = verdi(child(answer, "SvarNei"), null);
            assertThat(nei).isEqualTo(String.valueOf(!Boolean.parseBoolean(ja)));
            arbeidet += Boolean.parseBoolean(ja) ? 1 : 0;
        }

        assertThat((double) arbeidet / generated.size()).isCloseTo(expected, within(0.03));

    }

    @Test
    void shouldOverrideArbeidstimerWithPythonFormatting()
            throws Exception {

        var generator = generator();

        var integral = parse(generator.generateMeldekort(MeldekortType.ARBS, 1, 7.0, new SplittableRandom(3)).getFirst());
        var decimal = parse(generator.generateMeldekort(MeldekortType.ARBS, 1, 7.5, new SplittableRandom(3)).getFirst());

        assertThat(dager(integral)).allSatisfy(dag -> assertThat(verdi(dag, "ArbeidetTimerSum")).isEqualTo("7"));
        assertThat(dager(decimal)).allSatisfy(dag -> assertThat(verdi(dag, "ArbeidetTimerSum")).isEqualTo("7.5"));

    }

    @Test
    void shouldReturnEmptyListForZeroMeldekort() {
        assertThat(generator().generateMeldekort(MeldekortType.FY, 0, null, new SplittableRandom(1))).isEmpty();
    }

    @Test
    void shouldLoadAllMeldegrupperFromDirectory(@TempDir Path directory)
            throws IOException {

        for (var type : MeldekortType.values()) {
            writeFixture(directory, type.name());
        }
        writeFixture(directory, "DAGO");
        Files.writeString(directory.resolve("model_ARBS_distributions_col_1.onnx"), "ignored");

        var generated = MeldekortGenerator.fromDirectory(directory)
                .generateMeldekort(MeldekortType.INDIV, 2, null, new SplittableRandom(5));

        assertThat(generated).hasSize(2);

    }

    @Test
    void shouldFailWhenMeldegruppeModelIsMissing(@TempDir Path directory)
            throws IOException {

        writeFixture(directory, "ARBS");

        assertThatIllegalStateException()
                .isThrownBy(() -> MeldekortGenerator.fromDirectory(directory))
                .withMessageContaining("ATTF")
                .withMessageContaining("FY");

    }

    @Test
    void shouldRejectModelWhoseContentDoesNotMatchFilename(@TempDir Path directory)
            throws IOException {

        try (var input = BeanFixtures.open("bean/meldekort_bean_ARBS.json.gz")) {
            Files.copy(input, directory.resolve("meldekort_bean_DAGP.json.gz"));
        }

        assertThatIllegalStateException()
                .isThrownBy(() -> MeldekortGenerator.fromDirectory(directory))
                .withMessageContaining("contains meldegruppe ARBS");

    }

    private static MeldekortGenerator generator() {

        var synthesizer = BeanFixtures.synthesizer("ARBS");
        var synthesizers = new EnumMap<MeldekortType, BeanSynthesizer>(MeldekortType.class);
        Arrays.stream(MeldekortType.values()).forEach(type -> synthesizers.put(type, synthesizer));
        return new MeldekortGenerator(synthesizers);

    }

    private static void writeFixture(Path directory, String meldegruppe)
            throws IOException {

        var jsonMapper = JsonMapper.builder().build();
        ObjectNode document;
        try (var input = new GZIPInputStream(BeanFixtures.open("bean/meldekort_bean_ARBS.json.gz"))) {
            document = (ObjectNode) jsonMapper.readTree(input);
        }
        document.put("meldegruppe", meldegruppe);
        try (var output = new GZIPOutputStream(Files.newOutputStream(directory.resolve("meldekort_bean_" + meldegruppe + ".json.gz")))) {
            jsonMapper.writeValue(output, document);
        }

    }

    private static Element parse(String xml)
            throws Exception {

        var factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        var document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        return (Element) document.getElementsByTagName("Meldekort").item(0);

    }

    private static Element child(Element parent, String localName) {
        return (Element) parent.getElementsByTagNameNS(NS, localName).item(0);
    }

    private static List<Element> dager(Element meldekort) {

        var nodes = meldekort.getElementsByTagNameNS(NS, "MeldekortDag");
        var dager = new ArrayList<Element>();
        for (int index = 0; index < nodes.getLength(); index++) {
            dager.add((Element) nodes.item(index));
        }
        return dager;

    }

    private static String text(Element parent, String localName) {
        return parent.getElementsByTagNameNS(NS, localName).item(0).getTextContent();
    }

    private static String verdi(Element parent, String localName) {

        var container = localName == null ? parent : (Element) parent.getElementsByTagNameNS(NS, localName).item(0);
        return container.getElementsByTagNameNS(NS, "Verdi").item(0).getTextContent();

    }

}
