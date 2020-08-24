import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const SykdomPanel = ({ stateModifier }: any) => {
	const sm = stateModifier(SykdomPanel.initialValues)
	return (
		// @ts-ignore
		<Panel
			heading={SykdomPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="sykdom"
		>
			<AttributtKategori title={null}>
				<Attributt attr={sm.attrs.sykemelding} />
			</AttributtKategori>
		</Panel>
	)
}

SykdomPanel.heading = 'Sykdom'

SykdomPanel.initialValues = ({ set, del, has }: any) => ({
	sykemelding: {
		label: 'Har sykemelding',
		checked: has('sykemelding'),
		add() {
			set('sykemelding', {
				syntSykemelding: {
					startDato: new Date(),
					orgnummer: '',
					arbeidsforholdId: ''
				}
			})
		},
		remove() {
			del('sykemelding')
		}
	}
})
