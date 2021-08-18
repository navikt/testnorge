import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const KontaktDoedsboPanel = ({ stateModifier }) => {
	const sm = stateModifier(KontaktDoedsboPanel.initialValues)

	return (
		<Panel
			heading={KontaktDoedsboPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="doedsbo"
		>
			<AttributtKategori>
				<Attributt attr={sm.attrs.kontaktinformasjonForDoedsbo} />
			</AttributtKategori>
		</Panel>
	)
}

KontaktDoedsboPanel.heading = 'Kontaktinformasjon for dødsbo'

KontaktDoedsboPanel.initialValues = ({ set, del, has }) => ({
	kontaktinformasjonForDoedsbo: {
		label: 'Har kontaktinformasjon for dødsbo',
		checked: has('pdlforvalter.kontaktinformasjonForDoedsbo'),
		add() {
			set('pdlforvalter.kontaktinformasjonForDoedsbo', {
				adressat: { adressatType: '' },
				adresselinje1: '',
				adresselinje2: '',
				postnummer: '',
				poststedsnavn: '',
				landkode: 'NOR',
				skifteform: '',
				utstedtDato: ''
			})
		},
		remove() {
			del('pdlforvalter.kontaktinformasjonForDoedsbo')
		}
	}
})
