package no.nav.dolly.domain.resultset.udistub.model.opphold;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPeriode;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsUdiOppholdStatus {

    private UdiOppholdsrettType eosEllerEFTABeslutningOmOppholdsrett;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime eosEllerEFTABeslutningOmOppholdsrettEffektuering;
    private RsUdiPeriode eosEllerEFTABeslutningOmOppholdsrettPeriode;

    private UdiOppholdstillatelse eosEllerEFTAOppholdstillatelse;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime eosEllerEFTAOppholdstillatelseEffektuering;
    private RsUdiPeriode eosEllerEFTAOppholdstillatelsePeriode;

    private UdiVarighetOpphold eosEllerEFTAVedtakOmVarigOppholdsrett;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime eosEllerEFTAVedtakOmVarigOppholdsrettEffektuering;
    private RsUdiPeriode eosEllerEFTAVedtakOmVarigOppholdsrettPeriode;

    private RsUdiIkkeOppholdstilatelseIkkeVilkaarIkkeVisum ikkeOppholdstilatelseIkkeVilkaarIkkeVisum;

    private RsUdiOppholdSammeVilkaar oppholdSammeVilkaar;

    private Boolean uavklart;
}
