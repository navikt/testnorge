package no.nav.registre.core.converter;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;

public abstract class BaseConverter<S, T> implements Converter<S, T> {

    protected ConversionService registerConverter(ConversionService conversionService) {
        ((ConverterRegistry) conversionService).addConverter(this);
        return conversionService;
    }
}