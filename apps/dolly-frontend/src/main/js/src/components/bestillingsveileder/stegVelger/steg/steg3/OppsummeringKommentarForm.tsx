import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const OppsummeringKommentarForm = () => {
	return (
		<div className="input-oppsummering">
			<div className="flexbox--align-center">
				<h2>Send med kommentar</h2>
				<FormikTextInput name="beskrivelse" />
			</div>
		</div>
	)
}
