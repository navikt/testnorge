import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '~/components/ui/form/formUtils'
import { dokarkivAttributt } from '~/components/fagsystem/dokarkiv/form/DokarkivForm'

export const DokarkivPanel = ({ stateModifier, formikBag }: any) => {
	const sm = stateModifier(DokarkivPanel.initialValues)

	return (
		// @ts-ignore
		<Panel
			heading={DokarkivPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="dokarkiv"
			startOpen={harValgtAttributt(formikBag.values, [dokarkivAttributt])}
		>
			<AttributtKategori title="Oppretting av dokument" attr={sm.attrs}>
				<Attributt attr={sm.attrs.dokarkiv} disabled={sm.attrs.digitalInnsending.checked} />
				<Attributt attr={sm.attrs.digitalInnsending} disabled={sm.attrs.dokarkiv.checked} />
			</AttributtKategori>
		</Panel>
	)
}

DokarkivPanel.heading = 'Dokumenter'

DokarkivPanel.initialValues = ({ set, del, has }: any) => ({
	dokarkiv: {
		label: 'Skanning',
		checked: has('dokarkiv') && !has('dokarkiv.avsenderMottaker'),
		add() {
			set('dokarkiv', {
				tittel: '',
				tema: '',
				kanal: 'SKAN_IM',
				journalfoerendeEnhet: undefined,
				dokumenter: [
					{
						tittel: '',
						brevkode: '',
					},
				],
			})
		},
		remove() {
			del('dokarkiv')
		},
	},
	digitalInnsending: {
		label: 'Digital innsending',
		checked: has('dokarkiv.avsenderMottaker'),
		add() {
			set('dokarkiv', {
				tittel: '',
				tema: '',
				kanal: 'NAV_NO',
				avsenderMottaker: {
					id: '',
					navn: '',
					idType: '',
				},
				journalfoerendeEnhet: undefined,
				dokumenter: [
					{
						tittel: '',
						brevkode: '',
					},
				],
			})
		},
		remove() {
			del('dokarkiv')
		},
	},
})
