import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { InntektstubKodeverk } from '@/config/kodeverk'
import React from 'react'

const initialValues = {
	beloep: '',
	beskrivelse: null,
}

export const ForskuddstrekkForm = ({ inntektsinformasjonPath }) => {
	return (
		<FormDollyFieldArray
			name={`${inntektsinformasjonPath}.forskuddstrekksliste`}
			header="Forskuddstrekk"
			newEntry={initialValues}
		>
			{(path) => (
				<React.Fragment>
					<FormTextInput name={`${path}.beloep`} label="Beløp" type="number" />
					<FormSelect
						name={`${path}.beskrivelse`}
						label="Beskrivelse"
						kodeverk={InntektstubKodeverk.Forskuddstrekkbeskrivelse}
						size="large"
					/>
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
