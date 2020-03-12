import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import InntektStub from '~/components/inntektStub/validerInntekt'

const initialValues = {
	beloep: '',
	startOpptjeningsperiode: '',
	sluttOpptjeningsperiode: '',
	inntektstype: ''
}

export const InntektForm = ({ formikBag, inntektsinformasjonPath }) => {
	return (
		<FormikDollyFieldArray
			name={`${inntektsinformasjonPath}.inntektsliste`}
			header="Inntekt"
			newEntry={initialValues}
		>
			{path => (
				<React.Fragment>
					<FormikTextInput name={`${path}.beloep`} label="Beløp" type="number" />
					<FormikDatepicker
						name={`${path}.startOpptjeningsperiode`}
						label="Start opptjeningsperiode"
					/>
					<FormikDatepicker
						name={`${path}.sluttOpptjeningsperiode`}
						label="Slutt opptjeningsperiode"
					/>
					<InntektStub formikBag={formikBag} inntektPath={path} />
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
