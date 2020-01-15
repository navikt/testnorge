import React from 'react'
import _get from 'lodash/get'
import { ErrorMessage } from 'formik'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FieldArrayRemoveButton } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'

export const EnkeltinntektForm = ({ name, tjeneste, formikBag, fjern }) => {
	console.log('name :', name)
	// console.log('formikBag :', formikBag)
	const inntektArray = _get(formikBag.values, name, [])
	const grunnlag = inntektArray.length > 0
	const subHeader = name.includes('svalbard') ? 'Svalbard' : 'Fastlands-Norge'

	return (
		<div>
			{grunnlag && <h4>{subHeader}</h4>}
			{inntektArray.map((inntekt, idx) => (
				<div key={idx} className="flexbox">
					<FormikSelect
						name={`${name}[${idx}].tekniskNavn`}
						label="Type inntekt"
						kodeverk={Formatters.uppercaseAndUnderscoreToCapitalized(tjeneste)}
						size="grow"
						isClearable={false}
					/>
					<FormikTextInput name={`${name}[${idx}].verdi`} label="Beløp" type="number" />
					<FieldArrayRemoveButton onClick={() => fjern(idx, name, inntektArray)} />
				</div>
			))}
			<ErrorMessage name={name} className="error-message" component="div" />
			{/* <ErrorMessage name={name} className="error-message">
				{' '}
				{msg => <div>{msg}</div>}
			</ErrorMessage> */}
		</div>
	)
}
