import { TextEditor } from '@/components/ui/form/inputs/textEditor/TextEditor'
import * as _ from 'lodash-es'
import { CypressSelector } from '../../../../../../cypress/mocks/Selectors'

export const OppsummeringKommentarForm = ({ formikBag }) => {
	const eksisterendeBeskrivelse = _.get(formikBag.values, 'beskrivelse')
	return (
		<div className="input-oppsummering">
			<h2 data-cy={CypressSelector.TITLE_SEND_KOMMENTAR}>Send med kommentar</h2>
			<TextEditor
				text={null}
				handleSubmit={(value) => formikBag.setFieldValue('beskrivelse', value)}
				placeholder={eksisterendeBeskrivelse ? eksisterendeBeskrivelse : 'Skriv inn kommentar'}
			/>
		</div>
	)
}
