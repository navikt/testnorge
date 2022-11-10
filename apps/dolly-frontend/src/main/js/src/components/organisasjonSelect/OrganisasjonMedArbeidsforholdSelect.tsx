import * as React from 'react'
import { OrganisasjonSelect } from './OrganisasjonSelect'

type OrganisasjonMedArbeidsforholdSelectProps = {
	path: string
	label: string
	afterChange?: (value: any) => void
	valueNavn?: boolean
}

export const OrganisasjonMedArbeidsforholdSelect = (
	props: OrganisasjonMedArbeidsforholdSelectProps
) => <OrganisasjonSelect {...props} kanHaArbeidsforhold={true} />
