package no.nav.testnav.dollysearchservice.service;

import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class MultiSourceAccess implements CurrentTenantIdentifierResolver {

    @Override
    public @UnknownKeyFor @Nullable @Initialized Object resolveCurrentTenantIdentifier() {
        return null;
    }

    @Override
    public @UnknownKeyFor @NonNull @Initialized boolean validateExistingCurrentSessions() {
        return false;
    }
}
