import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialSivilstand } from '~/components/fagsystem/pdlf/form/initialValues'

export const FamilierelasjonPanel = ({ stateModifier }) => {
	const sm = stateModifier(FamilierelasjonPanel.initialValues)

	return (
		<Panel
			heading={FamilierelasjonPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType={'relasjoner'}
		>
			<AttributtKategori title="Sivilstand">
				<Attributt attr={sm.attrs.sivilstand} />
			</AttributtKategori>
		</Panel>
	)
}

FamilierelasjonPanel.heading = 'Familierelasjoner'

FamilierelasjonPanel.initialValues = ({ set, del, has, opts }) => ({
	sivilstand: {
		label: 'Sivilstand (har partner)',
		checked: has('pdldata.person.sivilstand'),
		add() {
			set('pdldata.person.sivilstand', [initialSivilstand])
		},
		remove() {
			del('pdldata.person.sivilstand')
		},
	},
})
