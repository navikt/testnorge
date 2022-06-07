import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialKontaktinfoForDoedebo } from '~/components/fagsystem/pdlf/form/initialValues'
import { harValgtAttributt } from '~/components/ui/form/formUtils'
import { doedsboAttributt } from '~/components/fagsystem/pdlf/form/partials/kontaktinformasjonForDoedsbo/KontaktinformasjonForDoedsbo'

export const KontaktDoedsboPanel = ({ stateModifier, formikBag }) => {
	const sm = stateModifier(KontaktDoedsboPanel.initialValues)

	return (
		<Panel
			heading={KontaktDoedsboPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="doedsbo"
			startOpen={harValgtAttributt(formikBag.values, [doedsboAttributt])}
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
