import * as React from 'react'
import { OrganisasjonSelect } from './OrganisasjonSelect'

type OrganisasjonMedArbeidsforholdSelectProps = {
	path: string
	label: string
	afterChange?: Function
	valueNavn?: boolean
}

export const OrganisasjonMedArbeidsforholdSelect = (
	props: OrganisasjonMedArbeidsforholdSelectProps
) => <OrganisasjonSelect {...props} kanHaArbeidsforhold={true} />
