import * as React from 'react'
import { OrganisasjonLoader } from './OrganisasjonLoader'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { EregResponse } from '~/service/Responses'

export type OrganisasjonSelectProps = {
	path: string
	label: string
	isClearable?: boolean
	afterChange?: Function
	valueNavn?: boolean
	filter?: (value: EregResponse) => boolean
}

export const OrganisasjonSelect = ({
	path,
	label,
	afterChange = null,
	valueNavn = false,
	filter = () => true
}: OrganisasjonSelectProps) => (
	<OrganisasjonLoader
		filter={filter}
		valueNavn={valueNavn}
		render={(liste, feilmelding) => (
			<FormikSelect
				name={path}
				label={label}
				options={liste}
				type="text"
				size="xlarge"
				isClearable={false}
				optionHeight={50}
				feil={feilmelding}
				afterChange={afterChange}
			/>
		)}
	/>
)
