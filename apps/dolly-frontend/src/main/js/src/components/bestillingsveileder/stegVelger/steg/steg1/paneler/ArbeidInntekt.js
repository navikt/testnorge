import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialValues } from '~/components/fagsystem/aareg/form/initialValues'
import { actions as fasteDataActions } from '~/ducks/fastedata'
import { harValgtAttributt } from '~/components/ui/form/formUtils'
import { aaregAttributt } from '~/components/fagsystem/aareg/form/Form'
import { sigrunAttributt } from '~/components/fagsystem/sigrunstub/form/Form'
import { inntektstubAttributt } from '~/components/fagsystem/inntektstub/form/Form'
import { inntektsmeldingAttributt } from '~/components/fagsystem/inntektsmelding/form/Form'

export const ArbeidInntektPanel = ({ stateModifier, formikBag }) => {
	const sm = stateModifier(ArbeidInntektPanel.initialValues)

	const infoTekst =
		'Arbeidsforhold: \nDataene her blir lagt til AAREG. \n\nInntekt: \nSkatte- og inntektsgrunnlag. Inntektene blir lagt i Sigrun-stub.' +
		'\n\nPensjonsgivende inntekt: \nInntektene blir lagt til i POPP-register. \n\nInntektstub: \nInformasjonen blir lagt i Inntekt-stub.'

	return (
		<Panel
			heading={ArbeidInntektPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arbeid"
			startOpen={harValgtAttributt(formikBag.values, [
				aaregAttributt,
				sigrunAttributt,
				inntektstubAttributt,
				inntektsmeldingAttributt,
			])}
		>
			<AttributtKategori title="Arbeidsforhold (Aareg)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.aareg} />
			</AttributtKategori>
			<AttributtKategori title="Skatteoppgjør (Sigrun)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.sigrunstub} />
			</AttributtKategori>
			<AttributtKategori title="A-ordningen (Inntektstub)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.inntektstub} />
			</AttributtKategori>
			<AttributtKategori title="Inntektsmelding (fra Altinn)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.inntektsmelding} />
			</AttributtKategori>
		</Panel>
	)
}

// TODO: Sett initialValue på virksomhet og opplysningspliktig til en random organisasjon, har ikke fått det til å fungere foreløpig

ArbeidInntektPanel.heading = 'Arbeid og inntekt'

ArbeidInntektPanel.initialValues = ({ set, del, has, dispatch }) => ({
	aareg: {
		label: 'Har arbeidsforhold',
		checked: has('aareg'),
		add: () => {
			dispatch(fasteDataActions.getFastedataOrganisasjoner())
			return set('aareg', [initialValues])
		},
		remove() {
			del('aareg')
		},
	},
	sigrunstub: {
		label: 'Har skattbar inntekt',
		checked: has('sigrunstub'),
		add: () =>
			set('sigrunstub', [
				{
					inntektsaar: new Date().getFullYear(),
					tjeneste: '',
					grunnlag: [],
					svalbardGrunnlag: [],
				},
			]),
		remove: () => del('sigrunstub'),
	},
	inntektsmelding: {
		label: 'Har inntektsmelding',
		checked: has('inntektsmelding'),
		add: () => {
			dispatch(fasteDataActions.getFastedataOrganisasjoner())
			return set('inntektsmelding', {
				inntekter: [
					{
						aarsakTilInnsending: 'NY',
						arbeidsgiver: {
							virksomhetsnummer: '',
						},
						arbeidsgiverPrivat: undefined,
						arbeidsforhold: {
							arbeidsforholdId: '',
							beregnetInntekt: {
								beloep: '',
							},
							foersteFravaersdag: '',
						},
						avsendersystem: {
							innsendingstidspunkt: new Date(),
						},
						refusjon: {
							refusjonsbeloepPrMnd: '',
							refusjonsopphoersdato: '',
						},
						naerRelasjon: false,
						ytelse: '',
					},
				],
				joarkMetadata: {
					tema: '',
				},
			})
		},
		remove: () => del('inntektsmelding'),
	},
	inntektstub: {
		label: 'Har inntekt',
		checked: has('inntektstub'),
		add: () => {
			dispatch(fasteDataActions.getFastedataOrganisasjoner())
			return set('inntektstub', {
				inntektsinformasjon: [
					{
						sisteAarMaaned: '',
						rapporteringsdato: null,
						antallMaaneder: '',
						virksomhet: '',
						opplysningspliktig: '',
						versjon: null,
						inntektsliste: [
							{
								beloep: '',
								startOpptjeningsperiode: '',
								sluttOpptjeningsperiode: '',
								inntektstype: '',
							},
						],
						fradragsliste: [],
						forskuddstrekksliste: [],
						arbeidsforholdsliste: [],
					},
				],
			})
		},
		remove() {
			del('inntektstub')
		},
	},
})
