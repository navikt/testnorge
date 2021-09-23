import React from 'react'
import { TextEditor } from '~/components/ui/form/inputs/textEditor/TextEditor'
import _get from 'lodash/get'

export const OppsummeringKommentarForm = ({ formikBag }) => {
	const eksisterendeBeskrivelse = _get(formikBag.values, 'beskrivelse')
	return (
		<div className="input-oppsummering">
			<h2>Send med kommentar</h2>
			<TextEditor
				text={null}
				handleSubmit={(value) => formikBag.setFieldValue('beskrivelse', value)}
				placeholder={eksisterendeBeskrivelse ? eksisterendeBeskrivelse : 'Skriv inn kommentar'}
			/>
		</div>
	)
}
