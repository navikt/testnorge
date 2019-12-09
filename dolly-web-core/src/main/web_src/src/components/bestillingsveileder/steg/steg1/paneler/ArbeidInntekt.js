import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'

export const ArbeidInntektPanel = ({ stateModifier }) => {
	const sm = stateModifier(ArbeidInntektPanel.initialValues)

	return (
		<Panel
			heading={ArbeidInntektPanel.heading}
			startOpen
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
		>
			<AttributtKategori title="Arbeidsforhold">
				<Attributt attr={sm.attrs.aareg} />
			</AttributtKategori>
			<AttributtKategori title="Inntekt">
				<Attributt attr={sm.attrs.sigrunstub} />
			</AttributtKategori>
		</Panel>
	)
}

ArbeidInntektPanel.heading = 'Arbeid og inntekt'

ArbeidInntektPanel.initialValues = ({ set, del, has }) => ({
	aareg: {
		label: 'Har arbeidsforhold',
		checked: has('aareg'),
		add: () =>
			set('aareg', [
				{
					ansettelsesPeriode: {
						fom: new Date(new Date().setFullYear(new Date().getFullYear() - 20)),
						tom: null
					},
					arbeidsforholdstype: 'ordinaertArbeidsforhold',
					arbeidsgiver: {
						aktoertype: '',
						orgnummer: '',
						ident: ''
					},
					arbeidsavtale: {
						yrke: '',
						stillingsprosent: 100,
						endringsdatoStillingsprosent: null,
						arbeidstidsordning: 'ikkeSkift',
						antallKonverterteTimer: '',
						avtaltArbeidstimerPerUke: 37.5
					},
					antallTimerForTimeloennet: [],
					permisjon: [],
					utenlandsopphold: []
				}
			]),
		remove() {
			del('aareg')
		}
	},
	sigrunstub: {
		label: 'Inntekt',
		checked: has('sigrunstub'),
		add: () =>
			set('sigrunstub', [
				{
					inntektsaar: new Date().getFullYear(),
					tjeneste: '',
					grunnlag: [],
					svalbardGrunnlag: []
				}
			]),
		remove: () => del('sigrunstub')
	}
})
