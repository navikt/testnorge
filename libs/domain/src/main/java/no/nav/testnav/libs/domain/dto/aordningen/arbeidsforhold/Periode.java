package no.nav.testnav.libs.domain.dto.aordningen.arbeidsforhold;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "fom", "tom" })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Periode {

    private LocalDate fom;
    private LocalDate tom;

    @JsonProperty("fom")
    @ApiModelProperty(
            notes = "Fra-og-med-dato for periode, format (ISO-8601): yyyy-MM-dd",
            example = "2014-07-01"
    )
    public String getFomAsString() {
        return JavaTimeUtil.toString(this.fom);
    }

    @JsonProperty("tom")
    @ApiModelProperty(
            notes = "Til-og-med-dato for periode, format (ISO-8601): yyyy-MM-dd",
            example = "2015-12-31"
    )
    public String getTomAsString() {
        return JavaTimeUtil.toString(this.tom);
    }

    protected Periode(PeriodeBuilder<?, ?> b) {
        this.fom = b.fom;
        this.tom = b.tom;
    }

    public static PeriodeBuilder<?, ?> builder() {
        return new PeriodeBuilderImpl();
    }

    private static final class PeriodeBuilderImpl extends
            PeriodeBuilder<Periode, PeriodeBuilderImpl> {

        private PeriodeBuilderImpl() {
        }

        protected PeriodeBuilderImpl self() {
            return this;
        }

        public Periode build() {
            return new Periode(this);
        }
    }

    public abstract static class PeriodeBuilder<C extends Periode, B extends PeriodeBuilder<C, B>> {

        private LocalDate fom;
        private LocalDate tom;

        public PeriodeBuilder() {
        }

        protected abstract B self();

        public abstract C build();

        public B fom(LocalDate fom) {
            this.fom = fom;
            return this.self();
        }

        public B tom(LocalDate tom) {
            this.tom = tom;
            return this.self();
        }

        public String toString() {
            return "Periode.PeriodeBuilder(fom=" + this.fom + ", tom=" + this.tom + ")";
        }
    }
}
