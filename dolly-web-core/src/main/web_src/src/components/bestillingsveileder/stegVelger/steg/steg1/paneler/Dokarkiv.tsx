import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const DokarkivPanel = ({ stateModifier }: any) => {
	const sm = stateModifier(DokarkivPanel.initialValues)

	return (
		// @ts-ignore
		<Panel
			heading={DokarkivPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="dokarkiv"
		>
			<AttributtKategori title="Oppretting av dokument">
				<Attributt attr={sm.attrs.dokarkiv} />
			</AttributtKategori>
		</Panel>
	)
}

DokarkivPanel.heading = 'Dokumenter'

DokarkivPanel.initialValues = ({ set, del, has }: any) => ({
	dokarkiv: {
		label: 'Skanning',
		checked: has('dokarkiv'),
		add() {
			set('dokarkiv', {
				tittel: '',
				tema: '',
				kanal: 'SKAN_IM',
				journalfoerendeEnhet: '',
				dokumenter: [
					{
						tittel: '',
						brevkode: ''
					}
				]
			})
		},
		remove() {
			del('dokarkiv')
		}
	}
})
