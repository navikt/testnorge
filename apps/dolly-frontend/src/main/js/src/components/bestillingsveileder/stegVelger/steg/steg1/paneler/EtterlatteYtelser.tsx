import Panel from '@/components/ui/panel/Panel'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import {
	Attributt,
	AttributtKategori,
} from '@/components/bestillingsveileder/stegVelger/steg/steg1/Attributt'

export const etterlatteYtelserAttributt = 'etterlatteYtelser'

export const EtterlatteYtelserPanel = ({ stateModifier, formValues }) => {
	const sm = stateModifier(EtterlatteYtelserPanel.initialValues)

	return (
		<Panel
			heading={EtterlatteYtelserPanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="grav"
			startOpen={harValgtAttributt(formValues, [etterlatteYtelserAttributt])}
		>
			<AttributtKategori attr={sm.attrs}>
				<Attributt attr={sm.attrs.etterlatteYtelser} />
			</AttributtKategori>
		</Panel>
	)
}

EtterlatteYtelserPanel.heading = 'Etterlatteytelser'

EtterlatteYtelserPanel.initialValues = ({ set, del, has }) => ({
	etterlatteYtelser: {
		label: 'Har etterlatteytelser',
		checked: has(etterlatteYtelserAttributt),
		add() {
			set(etterlatteYtelserAttributt, [{ ytelse: '', soeker: '' }])
		},
		remove() {
			del(etterlatteYtelserAttributt)
		},
	},
})
