import * as React from 'react'
import { OrganisasjonLoader } from '~/components/organisasjonSelect/OrganisasjonLoader'

export type OrganisasjonSelectProps = {
	path: string
	label?: string
	isClearable?: boolean
	afterChange?: (value) => void
	valueNavn?: boolean
	kanHaArbeidsforhold?: boolean
	value?: any
	feil?: any
}

export const OrganisasjonSelect = ({
	path,
	label,
	afterChange = null,
	valueNavn = false,
	kanHaArbeidsforhold,
	value,
	feil,
}: OrganisasjonSelectProps) => (
	<OrganisasjonLoader
		kanHaArbeidsforhold={kanHaArbeidsforhold}
		label={label}
		valueNavn={valueNavn}
		path={path}
		useFormikSelect={true}
		afterChange={afterChange}
		value={value}
		feil={feil}
	/>
)
