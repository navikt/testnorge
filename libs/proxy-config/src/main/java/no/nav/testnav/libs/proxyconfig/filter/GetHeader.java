package no.nav.testnav.libs.proxyconfig.filter;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class GetHeader {
    private final Supplier<String> name;
    private final Supplier<String> value;

    public String getName() {
        return name.get();
    }

    public String getValue() {
        return value.get();
    }

}
