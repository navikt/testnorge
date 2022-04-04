import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

const options = {
	personstatus: [
		{ value: 'BOSATT', label: 'Bosatt' },
		{ value: 'UTFLYTTET', label: 'Utflyttet' },
		{ value: 'FORSVUNNET', label: 'Forsvunnet' },
		{ value: 'DOED', label: 'Død' },
		{ value: 'OPPHOERT', label: 'Opphørt' },
		{ value: 'FOEDSELSREGISTRERT', label: 'Fødselsregistert' },
		{ value: 'IKKE_BOSATT', label: 'Ikke bosatt' },
		{ value: 'MIDLERTIDIG', label: 'Midlertidig' },
		{ value: 'INAKTIV', label: 'Inaktiv' },
	],
}

export const Personstatus = () => (
	<section>
		<FormikSelect
			name="personinformasjon.personstatus"
			label="Folkeregisterpersonstatus"
			options={options.personstatus}
			size="medium"
		/>
	</section>
)

export const PersonstatusPaths = {
	'personinformasjon.personstatus': 'string',
}
