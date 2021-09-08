import React from 'react'
import { useToggle } from 'react-use'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ifPresent, requiredString } from '~/utils/YupValidations'

export const MalForm = ({ formikBag }) => {
	const [aktiv, toggleMal] = useToggle(false)

	const _toggle = () => {
		formikBag.setFieldValue('malBestillingNavn', aktiv ? undefined : '')
		toggleMal()
	}

	//TODO: Sjekke om malnavn allerede finnes
	return (
		<div className="input-mal-field">
			<div className="flexbox--align-center">
				<h2>Lagre som mal</h2>
				<DollyCheckbox name="lagre-mal" onChange={_toggle} label="Lagre som mal" isSwitch />
			</div>
			<FormikTextInput name="malBestillingNavn" label="Malnavn" />
		</div>
	)
}

MalForm.validation = {
	malBestillingNavn: ifPresent('$malBestillingNavn', requiredString),
}
