import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { instAttributt } from '@/components/fagsystem/inst/form/Form'
import { runningE2ETest } from '@/service/services/Request'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useContext } from 'react'
import { getTimeoutAttr } from '@/components/bestillingsveileder/utils/timeoutTitle'

export const InstitusjonsoppholdPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(InstitusjonsoppholdPanel.initialValues)
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const instTimeout = getTimeoutAttr('INST', opts)
	return (
		<Panel
			heading={InstitusjonsoppholdPanel.heading}
			checkAttributeArray={sm.batchAdd as any}
			uncheckAttributeArray={sm.batchRemove as any}
			iconType="institusjon"
			startOpen={harValgtAttributt(formValues, [instAttributt])}
		>
			<AttributtKategori title="" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.instdata}
					disabled={instTimeout.disabled}
					title={instTimeout.title}
				/>
			</AttributtKategori>
		</Panel>
	)
}

InstitusjonsoppholdPanel.heading = 'Institusjonsopphold'

InstitusjonsoppholdPanel.initialValues = ({ set, del, has }: any) => ({
	instdata: {
		label: 'Har institusjonsopphold',
		checked: has('instdata'),
		add() {
			set('instdata', [
				{
					institusjonstype: runningE2ETest() ? 'AS' : '',
					startdato: runningE2ETest() ? new Date() : '',
					forventetSluttdato: '',
					sluttdato: '',
				},
			])
		},
		remove() {
			del('instdata')
		},
	},
})
