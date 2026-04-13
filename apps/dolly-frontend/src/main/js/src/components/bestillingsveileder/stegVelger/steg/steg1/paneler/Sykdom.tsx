import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { sykemeldingAttributt } from '@/components/fagsystem/sykdom/form/constants'
import { initialYrkesskade } from '@/components/fagsystem/yrkesskader/initialValues'
import { yrkesskaderAttributt } from '@/components/fagsystem/yrkesskader/form/Form'
import { initialValuesSykemelding } from '@/components/fagsystem/sykdom/form/initialValues'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useContext } from 'react'
import { getTimeoutAttr } from '@/components/bestillingsveileder/utils/timeoutTitle'

export const SykdomPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(SykdomPanel.initialValues)
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const yrkesskadeTimeout = getTimeoutAttr('YRKESSKADE', opts)
	const yrkesskadeTitle = yrkesskadeTimeout.title
	return (
		<Panel
			heading={SykdomPanel.heading}
			checkAttributeArray={(() => sm.batchAdd(['sykemelding'])) as any}
			uncheckAttributeArray={sm.batchRemove as any}
			iconType="sykdom"
			startOpen={harValgtAttributt(formValues, [sykemeldingAttributt, yrkesskaderAttributt])}
		>
			<AttributtKategori attr={sm.attrs}>
				<Attributt attr={sm.attrs.sykemelding} />
				<Attributt
					attr={sm.attrs.yrkesskader}
					disabled={yrkesskadeTimeout.disabled}
					title={yrkesskadeTitle}
				/>
			</AttributtKategori>
		</Panel>
	)
}

SykdomPanel.heading = 'Sykdom og skade'

SykdomPanel.initialValues = ({ set, del, has }: any) => ({
	sykemelding: {
		label: 'Har sykemelding',
		checked: has('sykemelding.nySykemelding'),
		add() {
			set('sykemelding.nySykemelding', initialValuesSykemelding)
		},
		remove() {
			del('sykemelding')
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
