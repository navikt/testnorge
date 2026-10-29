import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import {
	henvendelseAttributt,
	henvendelseMeldingerPath,
	henvendelseSamtalereferaterPath,
	initialMelding,
	initialSamtalereferat,
} from '@/components/fagsystem/henvendelse/initialValues'

export const SalesforcePanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(SalesforcePanel.initialValues)

	return (
		// @ts-ignore
		<Panel
			heading={SalesforcePanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="salesforce"
			startOpen={harValgtAttributt(formValues, [henvendelseAttributt])}
		>
			<AttributtKategori title="Henvendelse" attr={sm.attrs}>
				<Attributt attr={sm.attrs.meldinger} />
				<Attributt attr={sm.attrs.samtalereferater} />
			</AttributtKategori>
		</Panel>
	)
}

SalesforcePanel.heading = 'Salesforce'

SalesforcePanel.initialValues = ({ set, del, has }: any) => ({
	meldinger: {
		label: 'Har meldinger',
		checked: has(henvendelseMeldingerPath),
		add: () => set(henvendelseMeldingerPath, [initialMelding]),
		remove: () => del(henvendelseMeldingerPath),
	},
	samtalereferater: {
		label: 'Har samtalereferater',
		checked: has(henvendelseSamtalereferaterPath),
		add: () => set(henvendelseSamtalereferaterPath, [initialSamtalereferat]),
		remove: () => del(henvendelseSamtalereferaterPath),
	},
})
