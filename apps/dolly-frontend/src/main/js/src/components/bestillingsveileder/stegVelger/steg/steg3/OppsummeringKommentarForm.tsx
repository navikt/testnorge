import React from 'react'
import { TextEditor } from '~/components/ui/form/inputs/textEditor/TextEditor'
import { FormikBag } from 'formik'

export const OppsummeringKommentarForm = (formikBag: FormikBag<any, any>) => {
	return (
		<div className="input-oppsummering">
			<h2>Send med kommentar</h2>
			<TextEditor
				text={null}
				handleSubmit={(value) => {
					console.log('value: ', value) //TODO - SLETT MEG
					formikBag.setFieldValue('beskrivelse', value)
				}}
				placeholder="Skriv inn kommentar"
			/>
		</div>
	)
}
