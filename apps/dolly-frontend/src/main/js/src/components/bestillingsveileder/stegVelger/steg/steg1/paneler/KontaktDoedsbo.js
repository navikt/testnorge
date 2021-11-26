import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialKontaktinfoForDoedebo } from '~/components/fagsystem/pdlf/form/initialValues'

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
		checked: has('pdldata.person.kontaktinformasjonForDoedsbo'),
		add() {
			set('pdldata.person.kontaktinformasjonForDoedsbo', [initialKontaktinfoForDoedebo])
		},
		remove() {
			del('pdldata.person.kontaktinformasjonForDoedsbo')
		},
	},
})
