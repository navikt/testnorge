import * as React from 'react'
import { OrganisasjonLoader } from './OrganisasjonLoader'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

export type OrganisasjonSelectProps = {
	path: string
	label: string
	isClearable?: boolean
	afterChange?: Function
	valueNavn?: boolean
	kanHaArbeidsforhold?: boolean
}

export const OrganisasjonSelect = ({
	path,
	label,
	afterChange = null,
	valueNavn = false,
	kanHaArbeidsforhold
}: OrganisasjonSelectProps) => (
	<OrganisasjonLoader
		kanHaArbeidsforhold={kanHaArbeidsforhold}
		valueNavn={valueNavn}
		render={liste => (
			<FormikSelect
				name={path}
				label={label}
				options={liste}
				type="text"
				size="xlarge"
				isClearable={false}
				optionHeight={50}
				afterChange={afterChange}
			/>
		)}
	/>
)
