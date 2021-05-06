package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import java.time.LocalDate;
import java.util.List;

public interface Arbeidsforhold {
    boolean hasAvvik();

    String getArbeidsforholdId();

    String getArbeidsforholdType();

    String getYrke();

    Float getAntallTimerPerUkeSomEnFullStillingTilsvarer();

    LocalDate getSisteLoennsendringsdato();

    LocalDate getSisteDatoForStillingsprosentendring();

    String getAvloenningstype();

    String getArbeidstidsordning();

    Float getStillingsprosent();

    LocalDate getStartdato();

    LocalDate getSluttdato();

    String getSkipsregister();

    String getSkipstype();

    String getFartsomraade();

    String getKalendermaaned();

    String getOpplysningspliktigOrgnummer();

    String getVirksomhetOrgnummer();

    String getIdent();

    String getKildereferanse();

    int getAntallVelferdspermisjon();

    int getAntallPermisjonMedForeldrepenger();

    int getAntallPermittering();

    int getAntallPermisjon();

    int getAntallPermisjonVedMilitaertjeneste();

    int getAntallUtdanningspermisjon();

    List<? extends Permisjon> getPermisjoner();

    Integer getAntallInntekter();


    List<? extends Avvik> getAvvikList();

    String getAarsakTilSluttdato();

    String getFormForAnsettelse();
}
