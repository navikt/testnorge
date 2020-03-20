import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialValues } from '~/components/fagsystem/aareg/form/initialValues'
// import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

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
			<AttributtKategori title="Arbeidsforhold (Aareg)">
				<Attributt attr={sm.attrs.aareg} />
			</AttributtKategori>
			<AttributtKategori title="Skatteoppgjør (Sigrun)">
				<Attributt attr={sm.attrs.sigrunstub} />
			</AttributtKategori>
			<AttributtKategori title="Pensjonsgivende inntekt (POPP)">
				<Attributt attr={sm.attrs.pensjonforvalter} />
			</AttributtKategori>
			<AttributtKategori title="A-ordningen (Inntektskomponenten)">
				<Attributt attr={sm.attrs.inntektstub} />
			</AttributtKategori>
			<AttributtKategori title="Inntektsmelding (fra Altinn) - beta">
				<Attributt attr={sm.attrs.inntektsmelding} />
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
		label: 'Har inntekt',
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
	inntektsmelding: {
		label: 'Har inntektsmelding',
		checked: has('inntektsmelding'),
		add: () =>
			set('inntektsmelding', {
				inntekter: [
					{
						dato: new Date(),
						virksomhetsnummer: '',
						beloep: ''
					}
				]
			}),
		remove: () => del('inntektsmelding')
	},
	pensjonforvalter: {
		label: 'Har inntekt',
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
		label: 'Har inntekt',
		checked: has('inntektstub'),
		add: () =>
			set('inntektstub', {
				// prosentOekningPerAaar: null,
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
