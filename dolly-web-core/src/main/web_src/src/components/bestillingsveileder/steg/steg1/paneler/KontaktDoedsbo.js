import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'

export const KontaktDoedsboPanel = ({ stateModifier }) => {
	const sm = stateModifier(KontaktDoedsboPanel.initialValues)

	const infoTekst =
		'Kontaktinformasjon for dødsbo blir kun distribuert til Q2, og dette miljøet må derfor velges i siste steg.'

	return (
		<Panel
			heading={KontaktDoedsboPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="skull"
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
