import { TextEditor } from '@/components/ui/form/inputs/textEditor/TextEditor'
import { TestComponentSelectors } from '#/mocks/Selectors'

export const OppsummeringKommentarForm = ({ formMethods }) => {
	const eksisterendeBeskrivelse = formMethods.watch('beskrivelse')
	return (
		<div className="input-oppsummering">
			<h2 data-testid={TestComponentSelectors.TITLE_SEND_KOMMENTAR}>Send med kommentar</h2>
			<TextEditor
				text={null}
				handleSubmit={(value) => formMethods.setValue('beskrivelse', value)}
				placeholder={
					eksisterendeBeskrivelse ? eksisterendeBeskrivelse : 'Skriv inn kommentar (notat)'
				}
			/>
		</div>
	)
}
