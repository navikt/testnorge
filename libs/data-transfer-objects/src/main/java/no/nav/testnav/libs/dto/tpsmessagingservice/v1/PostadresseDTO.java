package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostadresseDTO {

    private PersonDTO person;

    private String postLinje1;

    private String postLinje2;

    private String postLinje3;

    private String postLand;

    public PostadresseDTO toUppercase() {
        if (isNotBlank(getPostLinje1())) {
            setPostLinje1(getPostLinje1().toUpperCase());
        }
        if (isNotBlank(getPostLinje2())) {
            setPostLinje2(getPostLinje2().toUpperCase());
        }
        if (isNotBlank(getPostLinje3())) {
            setPostLinje3(getPostLinje3().toUpperCase());
        }
        if (isNotBlank(getPostLand())) {
            setPostLand(getPostLand().toUpperCase());
        }
        return this;
    }
}
