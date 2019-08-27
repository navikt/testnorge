package no.nav.registre.bisys;

import java.lang.reflect.Field;
import no.nav.bidrag.ui.dto.BidragsmeldingConstant;
import no.nav.bidrag.ui.dto.SynthesizedBidragRequest;


public class Test {

  public static void main(String[] args) throws NoSuchFieldException, SecurityException {

    Field field =
        SynthesizedBidragRequest.class.getDeclaredField("boforholdBarnRegistrertPaaAdresse");

    BidragsmeldingConstant c = field.getAnnotation(BidragsmeldingConstant.class);
    System.out.println(field.getAnnotation(BidragsmeldingConstant.class).annotationType()
        .equals(BidragsmeldingConstant.class));

  }

}
