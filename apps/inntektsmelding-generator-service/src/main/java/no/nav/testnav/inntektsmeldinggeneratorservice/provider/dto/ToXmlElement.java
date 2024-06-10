package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

@FunctionalInterface
public interface ToXmlElement<T> {
    T toXmlElement();
}
