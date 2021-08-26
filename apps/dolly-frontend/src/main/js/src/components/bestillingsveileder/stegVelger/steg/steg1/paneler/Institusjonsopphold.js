import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const InstitusjonsoppholdPanel = ({ stateModifier }) => {
	const sm = stateModifier(InstitusjonsoppholdPanel.initialValues)

	return (
		<Panel
			heading={InstitusjonsoppholdPanel.heading}
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

InstitusjonsoppholdPanel.heading = 'Institusjonsopphold'

InstitusjonsoppholdPanel.initialValues = ({ set, del, has }) => ({
	instdata: {
		label: 'Har institusjonsopphold',
		checked: has('instdata'),
		add() {
			set('instdata', [
				{
					institusjonstype: '',
					startdato: '',
					sluttdato: ''
				}
			])
		},
		remove() {
			del('instdata')
		}
	}
})
