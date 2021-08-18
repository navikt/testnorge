import * as React from 'react'
import { OrganisasjonSelect } from './OrganisasjonSelect'

type OrganisasjonMedArbeidsforholdSelect = {
	path: string
	label: string
	afterChange?: Function
	valueNavn?: boolean
}

export const OrganisasjonMedArbeidsforholdSelect = (props: OrganisasjonMedArbeidsforholdSelect) => (
	<OrganisasjonSelect {...props} kanHaArbeidsforhold={true} />
)
