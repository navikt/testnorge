package no.nav.testnav.libs.dto.pdlforvalter.v1;

public enum VergemaalSakstype {

              MIM("Mindreårig midlertidig (unntatt EMF)"),
              ANN("Forvaltning utenfor vergemål"),
              VOK("Voksen"),
              MIN("Mindreårig (unntatt EMF)"),
              VOM("Voksen midlertidig"),
              FRE("Fremtidsfullmakt"),
              EMA("Enslig mindreårig asylsøker"),
              EMF("Enslig mindreårig flyktning inklusive midlertidige saker for denne gruppen");

    private final String forklaring;

    VergemaalSakstype (String forklaring) {
        this.forklaring = forklaring;
    }
}