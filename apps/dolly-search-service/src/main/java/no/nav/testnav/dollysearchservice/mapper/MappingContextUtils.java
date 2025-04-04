package no.nav.testnav.dollysearchservice.mapper;

import lombok.experimental.UtilityClass;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.MappingContextFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * The utility class to get {@link MappingContext} configured by Orika
 * and ready to be used by {@link ma.glasnost.orika.MapperFacade#map(java.lang.Object, java.lang.Class)}.
 */
@UtilityClass
public final class MappingContextUtils {

    private static final MappingContextFactory MAPPING_CONTEXT_FACTORY;

    static {
        MAPPING_CONTEXT_FACTORY = new CustomDefaultMapperFactory.Builder().build().getContextFactory();
    }

    /**
     * Gets {@link MappingContext} ready to be used by {@link ma.glasnost.orika.MapperFacade#map(java.lang.Object, java.lang.Class)}.
     * Manual creation of {@link MappingContext} does not work. Please see the link below.
     *
     * @return {@link MappingContext}
     * @see <a href="https://github.com/orika-mapper/orika/issues/354">https://github.com/orika-mapper/orika/issues/354</a>
     */
    public static MappingContext getMappingContext() {
        return MAPPING_CONTEXT_FACTORY.getContext();
    }

    /**
     * The custom {@link DefaultMapperFactory} to expose getter
     * for {@link MappingContextFactory} so {@link MappingContext} with prefilled globalProperties can be created.
     *
     * @author Dmitry Lebedko (dmitry.lebedko@t-systems.com)
     */
    private static class CustomDefaultMapperFactory extends DefaultMapperFactory {

        /**
         * Constructs a new instance of DefaultMapperFactory
         *
         * @param builder {@link MapperFactoryBuilder}
         */
        protected CustomDefaultMapperFactory(MapperFactoryBuilder<?, ?> builder) {
            super(builder);
        }

        /**
         * Gets {@link MappingContextFactory}.
         *
         * @return {@link MappingContextFactory}
         */
        public MappingContextFactory getContextFactory() {
            return contextFactory;
        }

        public static class Builder extends DefaultMapperFactory.MapperFactoryBuilder<CustomDefaultMapperFactory, Builder> {

            @Override
            public CustomDefaultMapperFactory build() {
                return new CustomDefaultMapperFactory(this);
            }

            @Override
            protected CustomDefaultMapperFactory.Builder self() {
                return this;
            }

        }

    }
}