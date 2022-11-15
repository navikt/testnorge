import * as React from 'react'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { useDollyFasteDataOrganisasjoner } from '~/utils/hooks/useOrganisasjoner'

type OrganisasjonLoaderProps = {
	kanHaArbeidsforhold?: boolean
	valueNavn?: boolean
	label?: string
	path: string
	handleChange?: (val) => void
	afterChange?: (val) => void
	value: any
	feil?: any
	useFormikSelect?: boolean
}

export const OrganisasjonLoader = ({
	kanHaArbeidsforhold,
	valueNavn = false,
	label,
	path,
	handleChange,
	useFormikSelect,
	feil,
	value,
	...props
}: OrganisasjonLoaderProps) => {
	const validEnhetstyper = ['BEDR', 'AAFY']

	const { loading, organisasjoner } = useDollyFasteDataOrganisasjoner(kanHaArbeidsforhold)

	const formatLabel = (org) => `${org.orgnummer} (${org.enhetstype}) - ${org.navn}`
	const organisasjonerSorted = organisasjoner
		.filter((virksomhet) =>
			kanHaArbeidsforhold ? validEnhetstyper.includes(virksomhet.enhetstype) : true
		)
		.sort(function (a, b) {
			if (a.opprinnelse < b.opprinnelse) {
				return 1
			}
			if (a.opprinnelse > b.opprinnelse) {
				return -1
			}
			return 0
		})
		.map((response) => ({
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
			label={label || 'Organisasjonsnummer'}
			type="text"
			isLoading={loading}
			options={organisasjonerSorted}
			size="xlarge"
			optionHeight={50}
			isClearable={false}
			{...props}
		/>
	) : (
		<DollySelect
			name={`${path}.orgNr`}
			label="Organisasjonsnummer"
			options={organisasjonerSorted}
			size="xlarge"
			isLoading={loading}
			onChange={handleChange}
			value={value}
			feil={feil}
			isClearable={false}
			{...props}
		/>
	)
}
