package no.nav.testnav.dollysearchservice.config;

import org.junit.jupiter.api.Test;

import static no.nav.testnav.dollysearchservice.config.CachingConfig.CACHE_REGISTRE;
import static no.nav.testnav.dollysearchservice.config.CachingConfig.CACHE_TESTNORGE_IDENTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class CachingConfigTest {

    private final CachingConfig cachingConfig = new CachingConfig();

    @Test
    void shouldRegisterBothNamedCaches() {

        var cacheManager = cachingConfig.cacheManager();

        assertThat(cacheManager.getCacheNames())
                .containsExactlyInAnyOrder(CACHE_TESTNORGE_IDENTER, CACHE_REGISTRE);
    }

    @Test
    void shouldSupportReactiveRetrieveForRegistreCache() {

        var cache = cachingConfig.cacheManager().getCache(CACHE_REGISTRE);

        assertThat(cache).isNotNull();
        assertThatNoException().isThrownBy(() -> cache.retrieve("ukjent-noekkel"));
    }

    @Test
    void shouldSupportReactiveRetrieveForTestnorgeIdenterCache() {

        var cache = cachingConfig.cacheManager().getCache(CACHE_TESTNORGE_IDENTER);

        assertThat(cache).isNotNull();
        assertThatNoException().isThrownBy(() -> cache.retrieve("ukjent-noekkel"));
    }

    @Test
    void shouldReturnCachedValueOnRetrieveAfterPut() {

        var cache = cachingConfig.cacheManager().getCache(CACHE_REGISTRE);

        assertThat(cache).isNotNull();
        cache.put("noekkel", "verdi");

        assertThat(cache.retrieve("noekkel")).isNotNull();
        assertThat(cache.get("noekkel", String.class)).isEqualTo("verdi");
    }
}
