package no.nav.registre.bisys.consumer.ui;

public class SoknadInputData {

  /*
   * <code>
   * Kombinasjoner av søktOm-sjekkbokser
   *
   * BIDRAG => bidragValgt && ikkeInnkrValgt
   * BIDRAG_INNKREVING => bidragValgt
   * ETTERGIVELSE => ettergivValgt
   * FORSKUDD => forskValgt
   * MOTREGNING => motregningValgt
   * OPPFOSTRINGSBIDRAG_INNKREVING => oppfostringValgt
   * SARTILSKUDD = sartilskValgt && ikkeInnkrValgt
   * SARTILSKUDD_INNKREVING => sartilskValgt
   * TILLEGGSBIDRAG => tillbidrValgt && ikkeInnkrValgt
   * TILLEGGSBIDRAG_INNKREVING => tillbidrValgt
   * TILBAKEKREVING => tilbakekrevValgt
   * TILBAKEKR_ETTERGIVELSE => tilbakekrevValgt && ettergivValgt
   * BIDRAG_18_AAR => ar18Valgt && ikkeInnkrValgt
   * BIDRAG_18_AAR_INNKREVING => ar18Valgt
   * BIDRAG_18_AAR_TILLEGGSBIDRAG => ar18Valgt && tillbidrValgt && ikkeInnkrValgt
   * BIDRAG_18_AAR_TILLEGGSBIDRAG_INNKREVING => ar18Valgt && tillbidrValgt
   *
   * </code>
   */
  public enum SoktOm {
    BIDRAG,
    BIDRAG_INNKREVING,
    ETTERGIVELSE,
    FORSKUDD,
    MOTREGNING,
    SARTILSKUDD,
    SARTILSKUDD_INNKREVING,
    TILLEGGSBIDRAG,
    TILLEGGSBIDRAG_INNKREVING,
    TILBAKEKREVING,
    TILBAKEKR_ETTERGIVELSE,
    VAR_18_AR,
    VAR_18_AR_INNKREVING,
    VAR_18_AR_TILLEGGSB,
    VAR_18_AR_TILLEGGSB_INNKREVING
  }

  /* Valg i nedtrekksliste annet. */
  public enum SoktOmAnnet {
    AVSKRIVNING,
    BARNEBORTFORING,
    DIREKTE_OPPGJOR,
    EKTEFELLEB_INNKREVING,
    EKTEFELLEBIDRAG,
    ERSTATNING,
    FARSKAP,
    KUNNSKAP_BIOLOGISK_FAR,
    MORSKAP,
    OPPFOSTRINGSBIDRAG_INNKREVING,
    REFUSJON_BIDRAG,
    SAKSOMKOSTNINGER
  }
}
