package no.nav.registre.arena.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.arena.domain.aap.andreokonomytelser.AndreOkonomYtelserV1;
import no.nav.registre.arena.domain.aap.institusjonsopphold.Institusjonsopphold;
import no.nav.registre.arena.domain.aap.medlemfolketrygden.MedlemFolketrygden;
import no.nav.registre.arena.domain.aap.periode.Periode;
import no.nav.registre.arena.domain.aap.gensaksopplysninger.Saksopplysning;
import no.nav.registre.arena.domain.aap.vilkaar.Vilkaar;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aap {
    private String aktivitetsfase;
    private List<AndreOkonomYtelserV1> andreOkonomYtelser;
    private String avbruddKode;
    private String begrunnelse;
    private String beslutter;
    private String datoMottatt;
    private String fraDato;
    private List<Saksopplysning> genSaksopplysninger;
    private List<Institusjonsopphold> institusjonsopphold;
    private String justertFra;
    private MedlemFolketrygden medlemFolketrygden;
    private Periode periode;
    private String tilDato;
    private String utfall;
    private String utskrift;
    private String vedtaksvariant;
    private List<Vilkaar> vilkaar;
}
