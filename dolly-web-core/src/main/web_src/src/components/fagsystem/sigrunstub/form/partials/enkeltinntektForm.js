import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FieldArrayRemoveButton } from '~/components/ui/form/formUtils'

export const EnkeltinntektForm = ({ name, formikBag }) => {
	const inntektArray = _get(formikBag.values, name, [])

	const fjern = idx => {
		const nyArray = inntektArray.filter((_, _idx) => _idx !== idx)
		formikBag.setFieldValue(name, nyArray)
	}

	return inntektArray.map((inntekt, idx) => (
		<div key={idx}>
			<FormikSelect
				name={`${name}[${idx}].tekniskNavn`}
				label="Type inntekt"
				kodeverk="Summert skattegrunnlag" // Må formateres fra SUMMERT_SKATTEGRUNNLAG til Summert skattegrunnlag
				size="grow"
			/>
			<FormikTextInput name={`${name}[${idx}].verdi`} label="Beløp" />
			<FieldArrayRemoveButton onClick={() => fjern(idx)} />
		</div>
	))
}
