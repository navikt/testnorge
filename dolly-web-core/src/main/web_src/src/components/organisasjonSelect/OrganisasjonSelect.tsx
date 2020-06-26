import * as React from 'react'
import { OrganisasjonLoader } from './OrganisasjonLoader'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { EregResponse } from '~/service/Responses'

export type OrganisasjonSelectProps = {
	path: string
	label: string
	isClearable?: boolean
	filter?: (value: EregResponse) => boolean
}

export const OrganisasjonSelect = ({
	path,
	label,
	filter = () => true
}: OrganisasjonSelectProps) => (
	<OrganisasjonLoader
		filter={filter}
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
			/>
		)}
	/>
)
