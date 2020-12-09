package no.nav.registre.testnorge.personexportapi.consumer.dto;

public enum DiskresjonskoderType {

    VABO("0", "Vanlig bosatt"),
    URIK("1", "I utenrikstjeneste"),
    MILI("2", "Militær"),
    SVAL("3", "Svalbard"),
    KLIE("4", "Klientadresse"),
    UFB("5", "Uten fast bobel"),
    SPSF("6", "Sperret adresse, strengt fortrolig"),  //kode 6
    SPFO("7", "Sperret adresse, fortrolig"),          //kode 7
    PEND("8", "Pendler");

    private String navn;
    private String kodeverdi;

    DiskresjonskoderType(final String kode, final String diskresjonskodeNavn){
        kodeverdi = kode;
        navn = diskresjonskodeNavn;
    }

    public String getName() {
        return navn;
    }

    public String getKodeverdi() {
        return kodeverdi;
    }
}