import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialValues } from '~/components/fagsystem/aareg/form/initialValues'
// import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

export const ArbeidInntektPanel = ({ stateModifier }) => {
	const sm = stateModifier(ArbeidInntektPanel.initialValues)

	const infoTekst =
		'Arbeidsforhold: \nDataene her blir lagt til AAREG. \n\nInntekt: \nSkatte- og inntektsgrunnlag. Inntektene blir lagt i Sigrun-stub. \nInntektskomponenten: Informasjonen blir lagt i Inntekt-stub.'

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
			<AttributtKategori title="Inntektskomponenten (A-ordningen)">
				<Attributt attr={sm.attrs.inntektstub} />
			</AttributtKategori>
		</Panel>
	)
}

// TODO: Sett initialValue på virksomhet og opplysningspliktig til en random organisasjon, har ikke fått det til å fingere foreløpig
// const randomVirksomhet = () => {
// 	const orgInfo = SelectOptionsOppslag('orgnr')
// 	const options = SelectOptionsOppslag.formatOptions(orgInfo)
// 	const randomNumber = Math.floor(Math.random() * options.length)
// 	if (options.length > 0) {
// 		return options[randomNumber].value
// 	}
// 	return ''
// }
// const initialVirksomhet = randomVirksomhet()

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
	inntektstub: {
		label: 'Har inntekter',
		checked: has('inntektstub'),
		add: () =>
			set('inntektstub', {
				// prosentOekningPerAaar: null,
				inntektsinformasjon: [
					{
						startAarMaaned: '',
						antallMaaneder: null,
						virksomhet: '',
						opplysningspliktig: '',
						inntektsliste: [
							{
								beloep: null,
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
