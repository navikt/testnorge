import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { InntektstubKodeverk } from '@/config/kodeverk'
import React from 'react'

const initialValues = {
	beloep: '',
	beskrivelse: '',
}

export const FradragForm = ({ inntektsinformasjonPath }) => {
	return (
		<FormDollyFieldArray
			name={`${inntektsinformasjonPath}.fradragsliste`}
			header="Fradrag"
			newEntry={initialValues}
		>
			{(path) => (
				<React.Fragment>
					<FormTextInput name={`${path}.beloep`} label="Beløp" type="number" />
					<FormSelect
						name={`${path}.beskrivelse`}
						label="Beskrivelse"
						kodeverk={InntektstubKodeverk.Fradragbeskrivelse}
						size="xlarge"
					/>
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
