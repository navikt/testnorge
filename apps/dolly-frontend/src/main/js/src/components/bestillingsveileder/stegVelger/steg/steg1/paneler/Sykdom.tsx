import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { sykdomAttributt } from '@/components/fagsystem/sykdom/form/Form'
import { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

export const SykdomPanel = ({ stateModifier, formikBag }: any) => {
	const sm = stateModifier(SykdomPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)

	const harGyldigSykemeldingBestilling = opts?.tidligereBestillinger?.some(
		(bestilling) =>
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
			startOpen={harValgtAttributt(formikBag.values, [sykdomAttributt])}
		>
			<AttributtKategori title={null} attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.sykemelding}
					disabled={harGyldigSykemeldingBestilling}
					title={harGyldigSykemeldingBestilling ? 'Personen har allerede sykemelding' : null}
				/>
			</AttributtKategori>
		</Panel>
	)
}

SykdomPanel.heading = 'Sykdom'

SykdomPanel.initialValues = ({ set, del, has }: any) => ({
	sykemelding: {
		label: 'Har sykemelding',
		checked: has('sykemelding'),
		add() {
			set('sykemelding', {
				syntSykemelding: {
					startDato: new Date(),
					orgnummer: '',
					arbeidsforholdId: '',
				},
			})
		},
		remove() {
			del('sykemelding')
		},
	},
})
