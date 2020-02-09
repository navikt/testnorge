import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const InstutisjonsoppholdPanel = ({ stateModifier }) => {
	const sm = stateModifier(InstutisjonsoppholdPanel.initialValues)

	return (
		<Panel
			heading={InstutisjonsoppholdPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="institusjon"
		>
			<AttributtKategori>
				<Attributt attr={sm.attrs.instdata} />
			</AttributtKategori>
		</Panel>
	)
}

InstutisjonsoppholdPanel.heading = 'Instutisjonsopphold'

InstutisjonsoppholdPanel.initialValues = ({ set, del, has }) => ({
	instdata: {
		label: 'Har instutisjonsopphold',
		checked: has('instdata'),
		add() {
			set('instdata', [
				{
					institusjonstype: '',
					startdato: '',
					faktiskSluttdato: ''
				}
			])
		},
		remove() {
			del('instdata')
		}
	}
})
