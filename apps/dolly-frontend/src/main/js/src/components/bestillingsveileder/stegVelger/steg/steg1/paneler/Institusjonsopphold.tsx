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
import { initialInst, initialInstTesting } from '@/components/fagsystem/inst/initialValues'

interface EksisterendeInstOpphold {
	institusjonstype?: string
	startdato?: string
	forventetSluttdato?: string
	sluttdato?: string
	oppholdId?: string
}

interface EksisterendeInstMiljo {
	miljo: string
	data?: EksisterendeInstOpphold[]
}

const mapEksisterendeInstData = (instdata?: EksisterendeInstMiljo[]) => {
	if (!Array.isArray(instdata)) {
		return []
	}
	return instdata.flatMap((miljoData) =>
		(miljoData.data ?? []).map((opphold) => ({
			institusjonstype: opphold.institusjonstype ?? initialInst.institusjonstype,
			startdato: opphold.startdato ?? initialInst.startdato,
			forventetSluttdato: opphold.forventetSluttdato ?? initialInst.forventetSluttdato,
			sluttdato: opphold.sluttdato ?? initialInst.sluttdato,
			oppholdId: opphold.oppholdId ?? null,
			miljo: miljoData.miljo,
		})),
	)
}

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
	const eksisterendeInstData = mapEksisterendeInstData(opts?.personFoerLeggTil?.instdata)

	return {
		instdata: {
			label: 'Institusjonsopphold',
			checked: has('instdata'),
			add() {
				if (runningE2ETest()) {
					set('instdata', [initialInstTesting])
					return
				}
				set('instdata', eksisterendeInstData.length > 0 ? eksisterendeInstData : [initialInst])
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
