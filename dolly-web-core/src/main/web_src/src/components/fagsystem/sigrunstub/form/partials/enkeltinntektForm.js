import React from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FieldArrayRemoveButton } from '~/components/ui/form/formUtils'
import Formatters from '~/utils/DataFormatter'

export const EnkeltinntektForm = ({ name, tjeneste, formikBag, fjern }) => {
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
					/>
					<FormikTextInput name={`${name}[${idx}].verdi`} label="Beløp" />
					<FieldArrayRemoveButton onClick={() => fjern(idx, name, inntektArray)} />
				</div>
			))}
		</div>
	)
}
