import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { dokarkivAttributt } from '@/components/fagsystem/dokarkiv/form/DokarkivForm'
import {
	initialDigitalInnsending,
	initialDokarkiv,
	initialHistark,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { histarkAttributt } from '@/components/fagsystem/histark/form/HistarkForm'

export const DokarkivPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(DokarkivPanel.initialValues)

	return (
		// @ts-ignore
		<Panel
			heading={DokarkivPanel.heading}
			checkAttributeArray={() => sm.batchAdd(['digitalInnsending', 'histark'])}
			uncheckAttributeArray={sm.batchRemove}
			iconType="dokarkiv"
			startOpen={harValgtAttributt(formValues, [dokarkivAttributt, histarkAttributt])}
		>
			<AttributtKategori title="Oppretting av dokument" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.dokarkiv}
					disabled={sm.attrs.digitalInnsending.checked || sm.attrs.histark.checked}
				/>
				<Attributt
					attr={sm.attrs.digitalInnsending}
					disabled={sm.attrs.dokarkiv.checked || sm.attrs.histark.checked}
				/>
				<Attributt
					attr={sm.attrs.histark}
					disabled={sm.attrs.dokarkiv.checked || sm.attrs.digitalInnsending.checked}
				/>
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
			set('dokarkiv', initialDokarkiv)
		},
		remove() {
			del('dokarkiv')
		},
	},
	digitalInnsending: {
		label: 'Digital innsending',
		checked: has('dokarkiv.avsenderMottaker'),
		add() {
			set('dokarkiv', initialDigitalInnsending)
		},
		remove() {
			del('dokarkiv')
		},
	},
	histark: {
		label: 'Histark',
		checked: has('histark'),
		add() {
			set('histark', initialHistark)
		},
		remove() {
			del('histark')
		},
	},
})
