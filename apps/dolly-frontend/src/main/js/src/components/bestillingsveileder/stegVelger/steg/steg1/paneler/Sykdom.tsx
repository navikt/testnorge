import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import {
	nySykemeldingAttributt,
	sykemeldingAttributt,
} from '@/components/fagsystem/sykdom/form/Form'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { initialYrkesskade } from '@/components/fagsystem/yrkesskader/initialValues'
import { yrkesskaderAttributt } from '@/components/fagsystem/yrkesskader/form/Form'
import {
	initialValuesDetaljertSykemelding,
	initialValuesNySykemelding,
} from '@/components/fagsystem/sykdom/form/initialValues'

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
			checkAttributeArray={() => sm.batchAdd(['nySykemelding'])}
			uncheckAttributeArray={sm.batchRemove}
			iconType="sykdom"
			startOpen={harValgtAttributt(formValues, [
				sykemeldingAttributt,
				nySykemeldingAttributt,
				yrkesskaderAttributt,
			])}
		>
			<AttributtKategori title={null} attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.sykemelding}
					disabled={harGyldigSykemeldingBestilling || sm.attrs.nySykemelding.checked}
					title={harGyldigSykemeldingBestilling ? 'Personen har allerede sykemelding' : null}
				/>
				<Attributt
					attr={sm.attrs.nySykemelding}
					disabled={sm.attrs.sykemelding.checked}
					title={
						sm.attrs.sykemelding.checked
							? 'Personen har allerede detaljert sykemelding i bestillingen'
							: null
					}
				/>
				<Attributt attr={sm.attrs.yrkesskader} />
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
