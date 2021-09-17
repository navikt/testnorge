import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { useToggle } from 'react-use'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { ifPresent, requiredString } from '~/utils/YupValidations'

export const OppsummeringKommentarForm = ({ formikBag }) => {
	const [aktiv, toggleBeskrivelse] = useToggle(false)

	const _toggle = () => {
		formikBag.setFieldValue('beskrivelse', aktiv ? undefined : '')
		toggleBeskrivelse()
	}

	return (
		<div className="input-oppsummering">
			<div className="flexbox--align-center">
				<h2>Send med kommentar</h2>
				<DollyCheckbox name="lagre-kommentar" onChange={_toggle} label="Med kommentar" isSwitch />
			</div>
			<FormikTextInput name="beskrivelse" label="Kommentar" />
		</div>
	)
}

OppsummeringKommentarForm.validation = {
	beskrivelse: ifPresent('$beskrivelse', requiredString),
}
