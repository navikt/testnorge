import React, { useContext } from 'react'
import Panel from '@/components/ui/panel/Panel'
import { Attributt } from '../Attributt'
import { AttributtKategori } from '../AttributtKategori'
import {
	fetchTpOrdninger,
	initialOrdning,
	tpPath,
} from '@/components/fagsystem/tjenestepensjon/form/Form'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { pensjonPath } from '@/components/fagsystem/pensjon/form/Form'
import { genInitialAlderspensjonVedtak } from '@/components/fagsystem/alderspensjon/form/initialValues'
import { initialUforetrygd } from '@/components/fagsystem/uforetrygd/initialValues'
import { alderspensjonPath } from '@/components/fagsystem/alderspensjon/form/Form'
import { uforetrygdPath } from '@/components/fagsystem/uforetrygd/form/Form'
import { initialPensjonsavtale } from '@/components/fagsystem/pensjonsavtale/initalValues'
import { initialAfpOffentlig } from '@/components/fagsystem/afpOffentlig/initialValues'
import { avtalePath } from '@/components/fagsystem/pensjonsavtale/form/Form'
import { afpOffentligPath } from '@/components/fagsystem/afpOffentlig/form/Form'
import { initialPensjonInntekt } from '@/components/fagsystem/pensjon/form/initialValues'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

export const PensjonPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(PensjonPanel.initialValues)

	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const harGyldigApBestilling = opts?.tidligereBestillinger?.some((bestilling) =>
		bestilling.status?.some(
			(status) => status.id === 'PEN_AP' && status.statuser?.some((item) => item?.melding === 'OK'),
		),
	)

	const harGyldigUforetrygdBestilling = opts?.tidligereBestillinger?.some((bestilling) =>
		bestilling.status?.some(
			(status) => status.id === 'PEN_UT' && status.statuser?.some((item) => item.melding === 'OK'),
		),
	)

	const infoTekst =
		'Pensjon: \nPensjonsgivende inntekt: \nInntektene blir lagt til i POPP-register. \n\n' +
		'Tjenestepensjon: \nTjenestepensjonsforhold lagt til i TP. \n\n' +
		'Alderspensjon: \nAlderspensjonssak med vedtak blir lagt til i PEN.'

	const getIgnoreKeys = () => {
		const ignoreKeys = []
		if (harGyldigApBestilling) {
			ignoreKeys.push('alderspensjon')
		}
		if (harGyldigUforetrygdBestilling) {
			ignoreKeys.push('uforetrygd')
		}
		return ignoreKeys
	}

	return (
		<Panel
			heading={PensjonPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={() => {
				sm.batchAdd(getIgnoreKeys())
			}}
			uncheckAttributeArray={sm.batchRemove}
			iconType="pensjon"
			startOpen={harValgtAttributt(formValues, [
				pensjonPath,
				avtalePath,
				tpPath,
				alderspensjonPath,
				uforetrygdPath,
				afpOffentligPath,
			])}
		>
			<AttributtKategori title="Pensjonsgivende inntekt (POPP)" attr={sm.attrs}>
				<Attributt item={sm.attrs.inntekt} id="inntekt_pensjon" />
			</AttributtKategori>
			<AttributtKategori title="Pensjonsavtale" attr={sm.attrs}>
				<Attributt item={sm.attrs.pensjonsavtale} />
			</AttributtKategori>
			<AttributtKategori title="Tjenestepensjon (TP)" attr={sm.attrs}>
				<Attributt item={sm.attrs.tp} />
			</AttributtKategori>
			<AttributtKategori title="Alderspensjon" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.alderspensjon}
					disabled={harGyldigApBestilling}
					title={harGyldigApBestilling ? 'Personen har allerede alderspensjon' : null}
				/>
			</AttributtKategori>
			<AttributtKategori title="Uføretrygd" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.uforetrygd}
					disabled={harGyldigUforetrygdBestilling}
					title={harGyldigUforetrygdBestilling ? 'Personen har allerede uføretrygd' : null}
				/>
			</AttributtKategori>
			<AttributtKategori title="AFP offentlig" attr={sm.attrs}>
				<Attributt item={sm.attrs.afpOffentlig} />
			</AttributtKategori>
		</Panel>
	)
}

PensjonPanel.heading = 'Pensjon'

PensjonPanel.initialValues = ({ set, del, has }: any) => {
	const paths = {
		inntekt: 'pensjonforvalter.inntekt',
		generertInntekt: 'pensjonforvalter.generertInntekt',
		tp: 'pensjonforvalter.tp',
		alderspensjon: 'pensjonforvalter.alderspensjon',
		uforetrygd: 'pensjonforvalter.uforetrygd',
		pensjonsavtale: 'pensjonforvalter.pensjonsavtale',
		afpOffentlig: 'pensjonforvalter.afpOffentlig',
	}
	return {
		inntekt: {
			label: 'Har inntekt',
			checked: has(paths.inntekt) || has(paths.generertInntekt),
			add: () => set(paths.inntekt, initialPensjonInntekt),
			remove: () => del([paths.inntekt, paths.generertInntekt]),
		},
		tp: {
			label: 'Har tjenestepensjonsforhold',
			checked: has(paths.tp),
			add: () => {
				fetchTpOrdninger()
				set(paths.tp, [initialOrdning])
			},
			remove: () => del(paths.tp),
		},
		alderspensjon: {
			label: 'Har alderspensjon',
			checked: has(paths.alderspensjon),
			add: () => {
				set(paths.alderspensjon, genInitialAlderspensjonVedtak)
			},
			remove: () => del(paths.alderspensjon),
		},
		uforetrygd: {
			label: 'Har uføretrygd',
			checked: has(paths.uforetrygd),
			add: () => {
				set(paths.uforetrygd, initialUforetrygd)
			},
			remove: () => del(paths.uforetrygd),
		},
		pensjonsavtale: {
			label: 'Har pensjonsavtale',
			checked: has(paths.pensjonsavtale),
			add: () => {
				set(paths.pensjonsavtale, initialPensjonsavtale)
			},
			remove: () => del(paths.pensjonsavtale),
		},
		afpOffentlig: {
			label: 'Har AFP offentlig',
			checked: has(paths.afpOffentlig),
			add: () => {
				set(paths.afpOffentlig, initialAfpOffentlig)
			},
			remove: () => del(paths.afpOffentlig),
		},
	}
}
