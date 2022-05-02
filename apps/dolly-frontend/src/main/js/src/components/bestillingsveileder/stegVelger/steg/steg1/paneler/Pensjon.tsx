import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const PensjonPanel = ({ stateModifier }: any) => {
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

PensjonPanel.initialValues = ({ set, del, has }: any) => ({
	inntekt: {
		label: 'Har inntekt',
		checked: has('pensjonforvalter.inntekt'),
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
		add: () =>
			set('pensjonforvalter.tp', {
				ordning: '3010',
			}),
		remove: () => del('pensjonforvalter.tp'),
	},
})
