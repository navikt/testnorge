import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import {
	fetchTpOrdninger,
	initialOrdning,
	tpPath,
} from '~/components/fagsystem/tjenestepensjon/form/Form'
import { harValgtAttributt } from '~/components/ui/form/formUtils'
import { pensjonPath } from '~/components/fagsystem/pensjon/form/Form'

export const PensjonPanel = ({ stateModifier, formikBag }: any) => {
	const sm = stateModifier(PensjonPanel.initialValues)

	const infoTekst =
		'Pensjon: \nPensjonsgivende inntekt: \nInntektene blir lagt til i POPP-register. \n\n' +
		'Tjenestepensjon: \nTjenestepensjonsforhold lagt til i TP.'

	return (
		<Panel
			heading={PensjonPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="pensjon"
			startOpen={harValgtAttributt(formikBag.values, [pensjonPath, tpPath])}
		>
			<AttributtKategori title="Pensjonsgivende inntekt (POPP)">
				<Attributt attr={sm.attrs.inntekt} />
			</AttributtKategori>
			<AttributtKategori title="Tjenestepensjon (TP)">
				<Attributt attr={sm.attrs.tp} />
			</AttributtKategori>
		</Panel>
	)
}

PensjonPanel.heading = 'Pensjon'

PensjonPanel.initialValues = ({ set, del, has }: any) => {
	const paths = {
		inntekt: 'pensjonforvalter.inntekt',
		tp: 'pensjonforvalter.tp',
	}
	return {
		inntekt: {
			label: 'Har inntekt',
			checked: has(paths.inntekt),
			add: () =>
				set('pensjonforvalter.inntekt', {
					fomAar: new Date().getFullYear() - 10,
					tomAar: null,
					belop: '',
					redusertMedGrunnbelop: true,
				}),
			remove: () => del('pensjonforvalter.inntekt'),
		},
		tp: {
			label: 'Har tjenestepensjonsforhold',
			checked: has('pensjonforvalter.tp'),
			add: () => {
				fetchTpOrdninger()
				set('pensjonforvalter.tp', [initialOrdning])
			},
			remove: () => del('pensjonforvalter.tp'),
		},
	}
}
