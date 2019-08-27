package no.nav.registre.bisys.consumer.ui.vedtak;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FatteVedtakRequest {

  private final String mottattdato;
  private final String soeknadFra;
  private final String soeknadsgrupper;
  private final String typeSoeknad;
  private final int enhet;

}
