import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import {
	nySykemeldingAttributt,
	sykemeldingAttributt,
} from '@/components/fagsystem/sykdom/form/Form'
import { initialYrkesskade } from '@/components/fagsystem/yrkesskader/initialValues'
import { yrkesskaderAttributt } from '@/components/fagsystem/yrkesskader/form/Form'
import {
	initialValuesDetaljertSykemelding,
	initialValuesNySykemelding,
} from '@/components/fagsystem/sykdom/form/initialValues'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useContext } from 'react'
import { getTimeoutAttr } from '@/components/bestillingsveileder/utils/timeoutTitle'

export const SykdomPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(SykdomPanel.initialValues)
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const sykemeldingTimeout = getTimeoutAttr('SYKEMELDING', opts)
	const yrkesskadeTimeout = getTimeoutAttr('YRKESSKADE', opts)
	const sykemeldingTitle = sykemeldingTimeout.title
	const nySykemeldingTitle =
		sykemeldingTitle ||
		(sm.attrs.sykemelding.checked
			? 'Personen har allerede detaljert sykemelding i bestillingen'
			: undefined)
	const yrkesskadeTitle = yrkesskadeTimeout.title
	return (
		<Panel
			heading={SykdomPanel.heading}
			checkAttributeArray={(() => sm.batchAdd(['nySykemelding'])) as any}
			uncheckAttributeArray={sm.batchRemove as any}
			iconType="sykdom"
			startOpen={harValgtAttributt(formValues, [
				sykemeldingAttributt,
				nySykemeldingAttributt,
				yrkesskaderAttributt,
			])}
		>
			<AttributtKategori title="" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.sykemelding}
					disabled={sm.attrs.nySykemelding.checked || sykemeldingTimeout.disabled}
					title={sykemeldingTitle}
				/>
				<Attributt
					attr={sm.attrs.nySykemelding}
					disabled={sm.attrs.sykemelding.checked || sykemeldingTimeout.disabled}
					title={nySykemeldingTitle}
				/>
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

SykdomPanel.initialValues = ({ set, del, delMutate, has }: any) => ({
	sykemelding: {
		label: 'Har detaljert sykemelding',
		checked: has('sykemelding.detaljertSykemelding'),
		add() {
			set('sykemelding.detaljertSykemelding', initialValuesDetaljertSykemelding)
		},
		remove() {
			del('sykemelding')
			delMutate?.()
		},
	},
	nySykemelding: {
		label: 'Har ny sykemelding',
		checked: has('sykemelding.nySykemelding'),
		add() {
			set('sykemelding.nySykemelding', initialValuesNySykemelding)
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
