import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialValues } from '~/components/fagsystem/aareg/form/initialValues'

export const ArbeidInntektPanel = ({ stateModifier }) => {
	const sm = stateModifier(ArbeidInntektPanel.initialValues)

	const infoTekst =
		'Arbeidsforhold: \nDataene her blir lagt til AAREG. \n\nInntekt: \nSkatte- og inntektsgrunnlag. Inntektene blir lagt i Sigrun-stub.' +
		'\n\nPensjonsgivende inntekt: \nInntektene blir lagt til i POPP-register. \n\nInntektskomponenten: \nInformasjonen blir lagt i Inntekt-stub.'

	return (
		<Panel
			heading={ArbeidInntektPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arbeid"
		>
			<AttributtKategori title="Arbeidsforhold">
				<Attributt attr={sm.attrs.aareg} />
			</AttributtKategori>
			<AttributtKategori title="Inntekt">
				<Attributt attr={sm.attrs.sigrunstub} />
			</AttributtKategori>
			<AttributtKategori title="Pensjonsgivende inntekt">
				<Attributt attr={sm.attrs.pensjonforvalter} />
			</AttributtKategori>
			<AttributtKategori title="Inntektskomponenten (A-ordningen)">
				<Attributt attr={sm.attrs.inntektstub} />
			</AttributtKategori>
		</Panel>
	)
}

// TODO: Sett initialValue på virksomhet og opplysningspliktig til en random organisasjon, har ikke fått det til å fungere foreløpig

ArbeidInntektPanel.heading = 'Arbeid og inntekt'

ArbeidInntektPanel.initialValues = ({ set, del, has }) => ({
	aareg: {
		label: 'Har arbeidsforhold',
		checked: has('aareg'),
		add: () => set('aareg', initialValues),
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
	},
	pensjonforvalter: {
		label: 'Pensjonsgivende inntekt',
		checked: has('pensjonforvalter'),
		add: () =>
			set('pensjonforvalter.inntekt', {
				fomAar: new Date().getFullYear() - 1,
				tomAar: null,
				belop: '',
				redusertMedGrunnbelop: true
			}),
		remove: () => del('pensjonforvalter.inntekt')
	},
	inntektstub: {
		label: 'Har inntekter',
		checked: has('inntektstub'),
		add: () =>
			set('inntektstub', {
				inntektsinformasjon: [
					{
						sisteAarMaaned: '',
						antallMaaneder: '',
						virksomhet: '',
						opplysningspliktig: '',
						inntektsliste: [
							{
								beloep: '',
								startOpptjeningsperiode: '',
								sluttOpptjeningsperiode: '',
								inntektstype: ''
							}
						],
						fradragsliste: [],
						forskuddstrekksliste: [],
						arbeidsforholdsliste: []
					}
				]
			}),
		remove() {
			del('inntektstub')
		}
	}
})
