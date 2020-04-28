import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const BrregPanel = ({ stateModifier }) => {
	const sm = stateModifier(BrregPanel.initialValues)
	// TODO: Infotekst?

	return (
		<Panel
			heading={BrregPanel.heading}
			// informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arbeid" // TODO: Fiks nytt ikon
		>
			<AttributtKategori>
				<Attributt attr={sm.attrs.bregstub} />
			</AttributtKategori>
		</Panel>
	)
}

BrregPanel.heading = 'Brønnøysundregistrene'

BrregPanel.initialValues = ({ set, del, has }) => ({
	bregstub: {
		label: 'Har roller i Brreg',
		checked: has('bregstub'),
		add() {
			set('bregstub', {
				understatuser: 0,
				enheter: [
					{
						rollekode: '',
						registreringsdato: new Date(),
						foretaksNavn: {
							navn1: ''
						},
						orgNr: ''
						// 922753458
					}
				]
			})
		},
		remove() {
			del('bregstub')
		}
	}
})
