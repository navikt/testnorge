import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

const initialValues = {
	beloep: null,
	beskrivelse: ''
}

export const ForskuddstrekkForm = ({ formikBag, inntektsinformasjonPath }) => {
	return (
		<FormikDollyFieldArray
			name={`${inntektsinformasjonPath}.forskuddstrekksliste`}
			title="Forskuddstrekk"
			newEntry={initialValues}
		>
			{path => (
				<React.Fragment>
					<FormikTextInput name={`${path}.beloep`} label="Beløp" type="number" />
					<FormikSelect
						name={`${path}.beskrivelse`}
						label="Beskrivelse"
						kodeverk="Forskuddstrekkbeskrivelse"
						size="large"
					/>
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
