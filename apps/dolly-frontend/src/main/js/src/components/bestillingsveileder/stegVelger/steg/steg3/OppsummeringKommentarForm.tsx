import { TestComponentSelectors } from '#/mocks/Selectors'
import { Textarea } from '@navikt/ds-react'

export const OppsummeringKommentarForm = ({ formMethods }) => {
	const eksisterendeBeskrivelse = formMethods.watch('beskrivelse')
	return (
		<div className="input-oppsummering">
			<Textarea
				data-testid={TestComponentSelectors.TITLE_SEND_KOMMENTAR}
				label={'Send med kommentar'}
				onChange={(event) => formMethods.setValue('beskrivelse', event.target.value)}
				placeholder={
					eksisterendeBeskrivelse ? eksisterendeBeskrivelse : 'Skriv inn kommentar (notat)'
				}
			/>
		</div>
	)
}
