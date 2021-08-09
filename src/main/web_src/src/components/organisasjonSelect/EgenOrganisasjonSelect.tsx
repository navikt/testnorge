import * as React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

type EgenOrganisasjonSelect = {
	name: string
	isClearable?: boolean
	onChange?: (selected: any) => void
	brukerId?: string
}

export const EgenOrganisasjonSelect = ({
	name,
	isClearable = true,
	onChange = null,
	brukerId
}: EgenOrganisasjonSelect) => {
	const virksomheter = SelectOptionsOppslag.hentVirksomheterFraOrgforvalter(brukerId)
	const virksomheterOptions = SelectOptionsOppslag.formatOptions('virksomheter', virksomheter)

	return (
		<FormikSelect
			name={name}
			label="Organisasjonsnummer"
			options={virksomheterOptions}
			size="xxlarge"
			isClearable={isClearable}
			onChange={onChange}
		/>
	)
}
