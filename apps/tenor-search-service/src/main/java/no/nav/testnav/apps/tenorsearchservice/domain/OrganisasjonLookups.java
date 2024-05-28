package no.nav.testnav.apps.tenorsearchservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@SuppressWarnings("java:S115")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrganisasjonLookups {

    Organisasjonsform(TenorOrganisasjonSelectOptions.OrganisasjonForm.class),
    ArbeidsforholdType(TenorOrganisasjonSelectOptions.ArbeidsforholdType.class),
    EnhetStatus(TenorOrganisasjonSelectOptions.EnhetStatus.class),
    Grunnlagsdata(TenorOrganisasjonSelectOptions.Grunnlagsdata.class);

    private final Class<? extends LabelEnum> value;
}