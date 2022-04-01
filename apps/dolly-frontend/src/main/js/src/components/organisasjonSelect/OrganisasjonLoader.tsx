import * as React from 'react'
import { useEffect } from 'react'
import { Organisasjon } from '~/service/services/organisasjonFasteDataService/OrganisasjonFasteDataService'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'

type OrganisasjonLoaderProps = {
	kanHaArbeidsforhold?: boolean
	valueNavn?: boolean
	label?: string
	organisasjoner: Organisasjon[]
	hentOrganisasjoner: Function
	path: string
	handleChange: Function
	afterChange: Function
	value: any
	feil?: any
	useFormikSelect?: boolean
}

export const OrganisasjonLoader = ({
	kanHaArbeidsforhold,
	valueNavn = false,
	label,
	organisasjoner,
	hentOrganisasjoner,
	path,
	handleChange,
	afterChange,
	useFormikSelect,
	feil,
	value,
}: OrganisasjonLoaderProps) => {
	useEffect(() => {
		hentOrganisasjoner('DOLLY', kanHaArbeidsforhold)
	}, [])

	if (!organisasjoner) return null

	const formatLabel = (org: Organisasjon) => `${org.orgnummer} (${org.enhetstype}) - ${org.navn}`
	const organisasjonerSorted = [...organisasjoner]
		.sort(function (a: Organisasjon, b: Organisasjon) {
			if (a.opprinnelse < b.opprinnelse) return 1
			if (a.opprinnelse > b.opprinnelse) return -1
			return 0
		})
		.map((response: Organisasjon) => ({
			value: valueNavn ? response.navn : response.orgnummer,
			label: formatLabel(response),
			orgnr: response.orgnummer,
			juridiskEnhet: response.overenhet,
			navn: response.navn,
			forretningsAdresse: response.forretningsAdresse,
			postadresse: response.postadresse,
		}))
	return useFormikSelect ? (
		<FormikSelect
			name={path}
			label={label ? label : 'Organisasjonsnummer'}
			type="text"
			options={organisasjonerSorted}
			size="xlarge"
			afterChange={afterChange}
			optionHeight={50}
			isClearable={false}
		/>
	) : (
		<DollySelect
			name={`${path}.orgNr`}
			label="Organisasjonsnummer"
			options={organisasjonerSorted}
			size="xlarge"
			onChange={handleChange}
			value={value}
			feil={feil}
			isClearable={false}
		/>
	)
}
