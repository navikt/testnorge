import { TextEditor } from '@/components/ui/form/inputs/textEditor/TextEditor'
import * as _ from 'lodash-es'

export const OppsummeringKommentarForm = ({ formikBag }) => {
	const eksisterendeBeskrivelse = _.get(formikBag.values, 'beskrivelse')
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
