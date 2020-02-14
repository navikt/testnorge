import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import InntektStub from '~/components/inntektStub/validerInntekt'

const initialValues = {
	beloep: '',
	startOpptjeningsperiode: null,
	sluttOpptjeningsperiode: null
}

export const InntektForm = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="inntektstub.inntektsinformasjon.inntektsliste"
			title="Inntekt"
			newEntry={initialValues}
		>
			{path => (
				<React.Fragment>
					<FormikTextInput name={`${path}.beloep`} label="Beløp" type="number" />
					<FormikDatepicker name={`${path}.startOpptjeningsperiode`} label="Startdato" />
					<FormikDatepicker name={`${path}.sluttOpptjeningsperiode`} label="Sluttdato" />
					{/* <InntektStub /> */}
					{/* sende med noe i denne ^ ?? */}
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
