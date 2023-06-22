package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EqualsAndHashCode(callSuper = true)
public class Gyldighetsperiode extends Periode {

    protected Gyldighetsperiode(GyldighetsperiodeBuilder<?, ?> b) {
        super(b);
    }

    public static GyldighetsperiodeBuilder<Gyldighetsperiode, GyldighetsperiodeBuilderImpl> builder() {
        return new GyldighetsperiodeBuilderImpl();
    }

    private static final class GyldighetsperiodeBuilderImpl extends GyldighetsperiodeBuilder<Gyldighetsperiode, GyldighetsperiodeBuilderImpl> {

        private GyldighetsperiodeBuilderImpl() {
        }

        protected GyldighetsperiodeBuilderImpl self() {
            return this;
        }

        public Gyldighetsperiode build() {
            return new Gyldighetsperiode(this);
        }
    }

    public abstract static class GyldighetsperiodeBuilder<C extends Gyldighetsperiode, B extends GyldighetsperiodeBuilder<C, B>> extends PeriodeBuilder<C, B> {

        protected GyldighetsperiodeBuilder() {
        }

        protected abstract B self();

        public abstract C build();

        @Override
        public String toString() {
            return "Gyldighetsperiode.GyldighetsperiodeBuilder(super=" + super.toString() + ")";
        }
    }
}

