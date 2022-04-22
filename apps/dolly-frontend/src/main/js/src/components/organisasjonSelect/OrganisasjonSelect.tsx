import * as React from 'react'
import OrganisasjonLoaderConnector from '~/components/organisasjonSelect/OrganisasjonLoaderConnector'

export type OrganisasjonSelectProps = {
	path: string
	label?: string
	isClearable?: boolean
	afterChange?: Function
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
	<OrganisasjonLoaderConnector
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
