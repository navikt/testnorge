import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { sykdomAttributt } from '@/components/fagsystem/sykdom/form/Form'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { initialYrkesskade } from '@/components/fagsystem/yrkesskader/initialValues'
import { yrkesskaderAttributt } from '@/components/fagsystem/yrkesskader/form/Form'
import { initialValuesDetaljertSykemelding } from '@/components/fagsystem/sykdom/form/initialValues'

export const SykdomPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(SykdomPanel.initialValues)
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const harGyldigSykemeldingBestilling = opts?.tidligereBestillinger?.some((bestilling) =>
		bestilling.status?.some(
			(status) =>
				status.id === 'SYKEMELDING' && status.statuser?.some((item) => item?.melding === 'OK'),
		),
	)

	return (
		// @ts-ignore
		<Panel
			heading={SykdomPanel.heading}
			checkAttributeArray={() => sm.batchAdd(harGyldigSykemeldingBestilling ? ['sykemelding'] : [])}
			uncheckAttributeArray={sm.batchRemove}
			iconType="sykdom"
			startOpen={harValgtAttributt(formValues, [sykdomAttributt, yrkesskaderAttributt])}
		>
			<AttributtKategori title={null} attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.sykemelding}
					disabled={harGyldigSykemeldingBestilling}
					title={harGyldigSykemeldingBestilling ? 'Personen har allerede sykemelding' : null}
				/>
				<Attributt attr={sm.attrs.yrkesskader} />
			</AttributtKategori>
		</Panel>
	)
}

SykdomPanel.heading = 'Sykdom og skade'

SykdomPanel.initialValues = ({ set, del, delMutate, has }: any) => ({
	sykemelding: {
		label: 'Har sykemelding',
		checked: has('sykemelding'),
		add() {
			set('sykemelding', initialValuesDetaljertSykemelding)
		},
		remove() {
			del('sykemelding')
			delMutate?.()
		},
	},
	yrkesskader: {
		label: 'Har yrkesskader',
		checked: has('yrkesskader'),
		add() {
			set('yrkesskader', [initialYrkesskade])
		},
		remove() {
			del('yrkesskader')
		},
	},
})
