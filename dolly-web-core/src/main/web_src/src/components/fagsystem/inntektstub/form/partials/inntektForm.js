import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import InntektStub from '~/components/inntektStub/validerInntekt'

const initialValues = {
	beloep: null,
	startOpptjeningsperiode: undefined,
	sluttOpptjeningsperiode: undefined,
	inntektstype: ''
}

export const InntektForm = ({ formikBag, inntektsinformasjonPath }) => {
	return (
		<FormikDollyFieldArray
			name={`${inntektsinformasjonPath}.inntektsliste`}
			title="Inntekt"
			newEntry={initialValues}
		>
			{path => (
				<React.Fragment>
					<FormikTextInput name={`${path}.beloep`} label="Beløp" type="number" />
					<FormikDatepicker name={`${path}.startOpptjeningsperiode`} label="Startdato" />
					<FormikDatepicker name={`${path}.sluttOpptjeningsperiode`} label="Sluttdato" />
					<InntektStub formikBag={formikBag} inntektPath={path} />
					{/* sende med noe i denne ^ ?? Og endre navn? */}
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
