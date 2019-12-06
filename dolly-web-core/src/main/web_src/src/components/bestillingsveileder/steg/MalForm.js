import React from 'react'
import * as Yup from 'yup'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { requiredString, ifPresent } from '~/utils/YupValidations'

export const MalForm = ({ formikBag }) => {
	//TODO: Sjekke om malnavn allerede finnes
	return (
		<div className="input-mal-field">
			<h3>Mal</h3>
			<div className="flexbox">
				<FormikSelect
					name="__lagreSomNyMal"
					label="Lagrer som mal"
					options={Options('boolean')}
					isClearable={false}
				/>
				{formikBag.values.__lagreSomNyMal && (
					<FormikTextInput name="malBestillingNavn" label="Malnavn" visHvisAvhuket={false} />
				)}
			</div>
		</div>
	)
}

MalForm.validation = {
	malBestillingNavn: Yup.string().when('__lagreSomNyMal', { is: true, then: requiredString })
}
