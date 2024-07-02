package no.nav.registre.testnorge.levendearbeidsforhold.domain;

public class PDLHendelse {
    private String folkeregisteridentifikator;
    private String hendelsetype;
    private String hendelsesdokument;
    private String persondokument;
    private String ajourholdstidspunkt;

    public PDLHendelse() {
    }

    public PDLHendelse(String folkeregisteridentifikator, String hendelsetype, String hendelsesdokument, String persondokument, String ajourholdstidspunkt) {
        this.folkeregisteridentifikator = folkeregisteridentifikator;
        this.hendelsetype = hendelsetype;
        this.hendelsesdokument = hendelsesdokument;
        this.persondokument = persondokument;
        this.ajourholdstidspunkt = ajourholdstidspunkt;
    }

    public String getFolkeregisteridentifikator() {
        return folkeregisteridentifikator;
    }

    public void setFolkeregisteridentifikator(String folkeregisteridentifikator) {
        this.folkeregisteridentifikator = folkeregisteridentifikator;
    }

    public String getHendelsetype() {
        return hendelsetype;
    }

    public void setHendelsetype(String hendelsetype) {
        this.hendelsetype = hendelsetype;
    }

    public String getHendelsesdokument() {
        return hendelsesdokument;
    }

    public void setHendelsesdokument(String hendelsesdokument) {
        this.hendelsesdokument = hendelsesdokument;
    }

    public String getPersondokument() {
        return persondokument;
    }

    public void setPersondokument(String persondokument) {
        this.persondokument = persondokument;
    }

    public String getAjourholdstidspunkt() {
        return ajourholdstidspunkt;
    }

    public void setAjourholdstidspunkt(String ajourholdstidspunkt) {
        this.ajourholdstidspunkt = ajourholdstidspunkt;
    }
}
