package no.nav.dolly.domain.resultset.tpsmessagingservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.data.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.data.kontoregister.v1.BankkontonrUtlandDTO;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsTpsMessaging {

    @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
    private LocalDate egenAnsattDatoFom;
    @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
    private LocalDate egenAnsattDatoTom;
    private BankkontonrUtlandDTO utenlandskBankkonto;
    private BankkontonrNorskDTO norskBankkonto;
}
