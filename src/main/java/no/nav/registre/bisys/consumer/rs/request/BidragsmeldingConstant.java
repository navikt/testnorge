package no.nav.registre.bisys.consumer.rs.request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * New properties that are not yet in the bidragsmelding received from NAV Syntetisereren.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BidragsmeldingConstant {
}
