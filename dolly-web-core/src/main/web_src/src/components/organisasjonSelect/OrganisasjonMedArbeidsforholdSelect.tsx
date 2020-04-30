import * as React from 'react'
import { OrganisasjonSelect } from './OrganisasjonSelect'

type OrganisasjonMedArbeidsforholdSelect = {
	path: string
	label: string
}

export const OrganisasjonMedArbeidsforholdSelect = (props: OrganisasjonMedArbeidsforholdSelect) => (
	<OrganisasjonSelect {...props} filter={org => org.kanHaArbeidsforhold} />
)
