import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { InntektstubKodeverk } from '~/config/kodeverk'

const initialValues = {
	beloep: '',
	beskrivelse: null,
}

export const ForskuddstrekkForm = ({ formikBag, inntektsinformasjonPath }) => {
	return (
		<FormikDollyFieldArray
			name={`${inntektsinformasjonPath}.forskuddstrekksliste`}
			header="Forskuddstrekk"
			newEntry={initialValues}
		>
			{(path) => (
				<React.Fragment>
					<FormikTextInput name={`${path}.beloep`} label="Beløp" type="number" />
					<FormikSelect
						name={`${path}.beskrivelse`}
						label="Beskrivelse"
						kodeverk={InntektstubKodeverk.Forskuddstrekkbeskrivelse}
						size="large"
					/>
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
