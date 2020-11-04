package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider.assertions;

import org.assertj.core.api.AbstractAssert;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.ArbeidsforholdDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.OpplysningspliktigDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.PersonDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.VirksomhetDTO;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider.matcher.CustomMatcher;

public class OpplysningspliktigDTOAssert extends AbstractAssert<OpplysningspliktigDTOAssert, OpplysningspliktigDTO> {
    public OpplysningspliktigDTOAssert(OpplysningspliktigDTO opplysningspliktigDTO) {
        super(opplysningspliktigDTO, OpplysningspliktigDTOAssert.class);
    }

    public static OpplysningspliktigDTOAssert assertThat(OpplysningspliktigDTO actual) {
        return new OpplysningspliktigDTOAssert(actual);
    }

    public OpplysningspliktigDTOAssert hasOpplysninspliktignummer(String opplysninspliktignummer) {
        isNotNull();

        if (actual.getOpplysningspliktigOrganisajonsnummer() == null && opplysninspliktignummer == null) {
            return this;
        }

        if (actual.getOpplysningspliktigOrganisajonsnummer() == null) {
            failWithMessage("Opplysningspliktignummer er 'null' forventet opplysninspliktignummer er [<%s>].", opplysninspliktignummer);
        }

        if (!actual.getOpplysningspliktigOrganisajonsnummer().equals(opplysninspliktignummer)) {
            failWithMessage("Opplysningspliktignummer er [<%s>], men forventet opplysninspliktignummer er [<%s>].",
                    actual.getOpplysningspliktigOrganisajonsnummer(),
                    opplysninspliktignummer
            );
        }

        return this;
    }


    public OpplysningspliktigDTOAssert hasKalendermaaned(LocalDate kalendermaaned) {
        isNotNull();

        if (actual.getKalendermaaned() == null && kalendermaaned == null) {
            return this;
        }

        if (actual.getKalendermaaned() == null) {
            failWithMessage("Kalendermaaned er 'null', men forventet kalendermaaned er [<%s>].", kalendermaaned);
        }

        if (!actual.getKalendermaaned().equals(kalendermaaned)) {
            failWithMessage("Kalendermaaned er [<%s>], men forventet kalendermaaned er [<%s>].",
                    actual.getOpplysningspliktigOrganisajonsnummer(),
                    kalendermaaned
            );
        }
        return this;
    }


    public OpplysningspliktigDTOAssert hasOneOfVirksomhentsnummer(String... virksomhentsnummer) {
        isNotNull();

        if (actual.getVirksomheter() == null || actual.getVirksomheter().isEmpty()) {
            failWithMessage("Finner ingen virksomhenter.");
        }

        if (virksomhentsnummer == null) {
            return this;
        }

        Set<String> vikrksomhentsnummerSet = Set.of(virksomhentsnummer);
        Optional<VirksomhetDTO> optional = actual.getVirksomheter().stream().filter(virksomhet ->
                vikrksomhentsnummerSet.contains(virksomhet.getOrganisajonsnummer())
        ).findAny();

        if (optional.isEmpty()) {
            failWithMessage("Finner ingen virksomhetner med virksomhetsnummer [<%s>].", String.join(",", virksomhentsnummer));
        }
        return this;
    }


    public OpplysningspliktigDTOAssert containsArbeidsforholdForIdent(ArbeidsforholdDTO expected, String ident, CustomMatcher<ArbeidsforholdDTO> customMatcher) {
        isNotNull();

        Optional<ArbeidsforholdDTO> optional = findArbeidsforholdForIdent(ident).stream().filter(arbeidsforhold -> customMatcher.match(arbeidsforhold, expected).isExactMatch()).findAny();

        if (optional.isEmpty()) {
            failWithMessage("Finner ikke arbeidsforhold [<%s>].", expected);
        }
        return this;
    }

    private List<ArbeidsforholdDTO> findArbeidsforholdForIdent(String ident) {
        return actual.getVirksomheter()
                .stream()
                .map(virksomhet -> virksomhet
                        .getPersoner()
                        .stream()
                        .filter(person -> person.getIdent().equals(ident))
                        .map(PersonDTO::getArbeidsforhold).flatMap(Collection::stream)
                        .collect(Collectors.toList())
                ).flatMap(Collection::stream).collect(Collectors.toList());
    }
}
