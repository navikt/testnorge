import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { initialHistark } from '@/components/fagsystem/histark/form/initialValues'
import {
	initialDigitalInnsending,
	initialDokarkiv,
} from '@/components/fagsystem/dokarkiv/form/initialValues'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { getTimeoutAttr } from '@/components/bestillingsveileder/utils/timeoutTitle'

export const DokarkivPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(DokarkivPanel.initialValues)
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const dokarkiv = getTimeoutAttr('DOKARKIV', opts)
	const histark = getTimeoutAttr('HISTARK', opts)
	return (
		<Panel
			heading={DokarkivPanel.heading}
			checkAttributeArray={() => sm.batchAdd(['digitalInnsending', 'histark'])}
			uncheckAttributeArray={sm.batchRemove as any}
			iconType="dokarkiv"
			startOpen={harValgtAttributt(formValues, ['dokarkiv', 'histark'])}
		>
			<AttributtKategori title="Oppretting av dokument" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.dokarkiv}
					disabled={
						dokarkiv.disabled || sm.attrs.digitalInnsending.checked || sm.attrs.histark.checked
					}
					title={dokarkiv.title}
				/>
				<Attributt
					attr={sm.attrs.digitalInnsending}
					disabled={dokarkiv.disabled || sm.attrs.dokarkiv.checked || sm.attrs.histark.checked}
					title={dokarkiv.title}
				/>
				<Attributt
					attr={sm.attrs.histark}
					disabled={
						histark.disabled || sm.attrs.dokarkiv.checked || sm.attrs.digitalInnsending.checked
					}
					title={histark.title}
				/>
			</AttributtKategori>
		</Panel>
	)
}

DokarkivPanel.heading = 'Dokumenter'

DokarkivPanel.initialValues = ({ set, del, has }: any) => ({
	dokarkiv: {
		label: 'Skanning',
		checked: has('dokarkiv') && !has('dokarkiv[0].avsenderMottaker'),
		add() {
			set('dokarkiv', [initialDokarkiv])
		},
		remove() {
			del('dokarkiv')
		},
	},
	digitalInnsending: {
		label: 'Digital innsending',
		checked: has('dokarkiv[0].avsenderMottaker'),
		add() {
			set('dokarkiv', [initialDigitalInnsending])
		},
		remove() {
			del('dokarkiv')
		},
	},
	histark: {
		label: 'Histark',
		checked: has('histark'),
		add() {
			set('histark', { dokumenter: [initialHistark] })
		},
		remove() {
			del('histark')
		},
	},
})
