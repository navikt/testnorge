import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'

export const henvendelseAttributt = 'henvendelse'

export const henvendelseInitialValues = {}

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
			<AttributtKategori attr={sm.attrs}>
				<Attributt attr={sm.attrs.henvendelse} />
			</AttributtKategori>
		</Panel>
	)
}

SalesforcePanel.heading = 'Salesforce'

SalesforcePanel.initialValues = ({ set, del, has }: any) => ({
	henvendelse: {
		label: 'Har henvendelse',
		checked: has(henvendelseAttributt),
		add: () => set(henvendelseAttributt, henvendelseInitialValues),
		remove: () => del(henvendelseAttributt),
	},
})
