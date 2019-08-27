package no.nav.registre.bisys.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BisysRequestAugments {

  private final String boforholdAndelForsorging;
  private final boolean boforholdBarnRegistrertPaaAdresse;
  private final String bidragsberegningKodeVirkAarsak;
  private final String bidragsberegningSamvarsklasse;
  private final String fatteVedtakGebyrBeslAarsakKode;

}
