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
import {
	initialKdi,
	initialKdiTesting,
	instdataKdiAttributt,
} from '@/components/fagsystem/kdi/initialValues'

export const InstitusjonsoppholdPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(InstitusjonsoppholdPanel.initialValues)
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const instTimeout = getTimeoutAttr('INST', opts)
	const kdiTimeout = getTimeoutAttr('INST_KDI', opts)
	return (
		<Panel
			heading={InstitusjonsoppholdPanel.heading}
			checkAttributeArray={sm.batchAdd as any}
			uncheckAttributeArray={sm.batchRemove as any}
			iconType="institusjon"
			startOpen={harValgtAttributt(formValues, [instAttributt, instdataKdiAttributt])}
		>
			<AttributtKategori attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.instdata}
					disabled={instTimeout.disabled}
					title={instTimeout.title}
				/>
				<Attributt
					attr={sm.attrs.instdataKdi}
					disabled={kdiTimeout.disabled}
					title={kdiTimeout.title}
				/>
			</AttributtKategori>
		</Panel>
	)
}

InstitusjonsoppholdPanel.heading = 'Institusjonsopphold'

InstitusjonsoppholdPanel.initialValues = ({ set, opts, del, has }: any) => {
	const eksisterendeKdiData = opts?.personFoerLeggTil?.instdataKdi

	return {
		instdata: {
			label: 'Institusjonsopphold',
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
		instdataKdi: {
			label: 'KDI-meldinger',
			checked: has('instdataKdi'),
			add() {
				set(
					'instdataKdi',
					runningE2ETest() ? initialKdiTesting : (eksisterendeKdiData ?? initialKdi),
				)
			},
			remove() {
				del('instdataKdi')
			},
		},
	}
}
