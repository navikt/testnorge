import React, { useContext } from 'react'
import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import {
	fetchTpOrdninger,
	initialOrdning,
	tpPath,
} from '@/components/fagsystem/tjenestepensjon/form/Form'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { pensjonPath } from '@/components/fagsystem/pensjon/form/Form'
import { genInitialAlderspensjonVedtak } from '@/components/fagsystem/alderspensjon/form/initialValues'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { initialUforetrygd } from '@/components/fagsystem/uforetrygd/initialValues'
import { runningE2ETest } from '@/service/services/Request'
import _ from 'lodash'
import { alderspensjonPath } from '@/components/fagsystem/alderspensjon/form/Form'
import { uforetrygdPath } from '@/components/fagsystem/uforetrygd/form/Form'
import {
	initialPensjonsavtale,
	initialUtbetalingsperiodeVerdier,
} from '@/components/fagsystem/pensjonsavtale/initalValues'

export const PensjonPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(PensjonPanel.initialValues)
	const opts = useContext(BestillingsveilederContext)

	const harValgtAp = _.has(formValues, 'pensjonforvalter.alderspensjon')
	const harValgtUforetrygd = _.has(formValues, 'pensjonforvalter.uforetrygd')

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

	const getTitleAlderspensjon = () => {
		if (harGyldigApBestilling) {
			return 'Personen har allerede alderspensjon'
		} else if (harGyldigUforetrygdBestilling) {
			return 'Personen har allerede uføretrygd'
		} else if (harValgtUforetrygd) {
			return 'Person kan ikke ha alderspensjon og uføretrygd samtidig'
		}
		return null
	}

	const getTitleUforetrygd = () => {
		if (harGyldigUforetrygdBestilling) {
			return 'Personen har allerede uføretrygd'
		} else if (harGyldigApBestilling) {
			return 'Personen har allerede alderspensjon'
		} else if (harValgtAp) {
			return 'Person kan ikke ha uføretrygd og alderspensjon samtidig'
		}
		return null
	}

	const infoTekst =
		'Pensjon: \nPensjonsgivende inntekt: \nInntektene blir lagt til i POPP-register. \n\n' +
		'Tjenestepensjon: \nTjenestepensjonsforhold lagt til i TP. \n\n' +
		'Alderspensjon: \nAlderspensjonssak med vedtak blir lagt til i PEN.'

	const getIgnoreKeys = () => {
		const ignoreKeys = []
		if (harGyldigApBestilling || harGyldigUforetrygdBestilling || harValgtUforetrygd) {
			ignoreKeys.push('alderspensjon')
		}
		if (harGyldigUforetrygdBestilling || harGyldigApBestilling || !harValgtUforetrygd) {
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
				tpPath,
				alderspensjonPath,
				uforetrygdPath,
			])}
		>
			<AttributtKategori title="Pensjonsgivende inntekt (POPP)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.inntekt} id="inntekt_pensjon" />
			</AttributtKategori>
			<AttributtKategori title="Pensjonsavtale" attr={sm.attrs}>
				<Attributt attr={sm.attrs.pensjonsavtale} />
			</AttributtKategori>
			<AttributtKategori title="Tjenestepensjon (TP)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.tp} />
			</AttributtKategori>
			<AttributtKategori title="Alderspensjon" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.alderspensjon}
					disabled={harGyldigApBestilling || harGyldigUforetrygdBestilling || harValgtUforetrygd}
					title={getTitleAlderspensjon()}
				/>
			</AttributtKategori>
			<AttributtKategori title="Uføretrygd" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.uforetrygd}
					disabled={harGyldigUforetrygdBestilling || harGyldigApBestilling || harValgtAp}
					title={getTitleUforetrygd()}
				/>
			</AttributtKategori>
		</Panel>
	)
}

PensjonPanel.heading = 'Pensjon'

PensjonPanel.initialValues = ({ set, del, has }: any) => {
	const paths = {
		inntekt: 'pensjonforvalter.inntekt',
		tp: 'pensjonforvalter.tp',
		alderspensjon: 'pensjonforvalter.alderspensjon',
		uforetrygd: 'pensjonforvalter.uforetrygd',
		pensjonsavtale: 'pensjonforvalter.pensjonsavtale',
	}
	return {
		inntekt: {
			label: 'Har inntekt',
			checked: has(paths.inntekt),
			add: () =>
				set(paths.inntekt, {
					fomAar: new Date().getFullYear() - 10,
					tomAar: runningE2ETest() ? new Date().getFullYear() : null,
					belop: runningE2ETest() ? '12345' : '',
					redusertMedGrunnbelop: true,
				}),
			remove: () => del(paths.inntekt),
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
	}
}
