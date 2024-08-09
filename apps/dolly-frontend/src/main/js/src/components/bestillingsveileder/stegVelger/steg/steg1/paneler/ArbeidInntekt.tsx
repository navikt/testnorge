import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialArbeidsforholdOrg } from '@/components/fagsystem/aareg/form/initialValues'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { aaregAttributt } from '@/components/fagsystem/aareg/form/Form'
import { sigrunAttributt } from '@/components/fagsystem/sigrunstub/form/Form'
import { inntektstubAttributt } from '@/components/fagsystem/inntektstub/form/Form'
import { inntektsmeldingAttributt } from '@/components/fagsystem/inntektsmelding/form/Form'
import {
	getInitialSigrunstubPensjonsgivende,
	sigrunstubPensjonsgivendeAttributt,
} from '@/components/fagsystem/sigrunstubPensjonsgivende/form/Form'
import {
	initialArbeidsgiverSkatt,
	skattekortAttributt,
} from '@/components/fagsystem/skattekort/form/Form'

export const ArbeidInntektPanel = ({ stateModifier, formValues }) => {
	const sm = stateModifier(ArbeidInntektPanel.initialValues)

	const infoTekst =
		'Arbeidsforhold: \nDataene her blir lagt til AAREG. \n\nInntekt: \nSkatte- og inntektsgrunnlag. Inntektene blir lagt i Sigrun-stub.' +
		'\n\nPensjonsgivende inntekt: \nInntektene blir lagt til i POPP-register. \n\nInntektstub: \nInformasjonen blir lagt i Inntekt-stub.' +
		'\n\nSkattekort: Dataene blir lagt til i SOKOS.'

	return (
		<Panel
			heading={ArbeidInntektPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arbeid"
			startOpen={harValgtAttributt(formValues, [
				aaregAttributt,
				sigrunAttributt,
				inntektstubAttributt,
				inntektsmeldingAttributt,
				sigrunstubPensjonsgivendeAttributt,
				skattekortAttributt,
			])}
		>
			<AttributtKategori title="Arbeidsforhold (Aareg)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.aareg} />
			</AttributtKategori>
			<AttributtKategori title="Inntekt (Sigrun)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.sigrunstub} />
				<Attributt attr={sm.attrs.sigrunstubPensjonsgivende} />
			</AttributtKategori>
			<AttributtKategori title="A-ordningen (Inntektstub)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.inntektstub} />
			</AttributtKategori>
			<AttributtKategori title="Inntektsmelding (fra Altinn)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.inntektsmelding} id="inntekt_inntektstub" />
			</AttributtKategori>
			<AttributtKategori title="Skattekort (SOKOS)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.skattekort} />
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
		add: () => set('aareg', [initialArbeidsforholdOrg]),
		remove() {
			del('aareg')
		},
	},
	sigrunstub: {
		label: 'Har lignet inntekt',
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
	sigrunstubPensjonsgivende: {
		label: 'Har pensjonsgivende inntekt',
		checked: has('sigrunstubPensjonsgivende'),
		add: () => set('sigrunstubPensjonsgivende', [getInitialSigrunstubPensjonsgivende()]),
		remove: () => del('sigrunstubPensjonsgivende'),
	},
	inntektsmelding: {
		label: 'Har inntektsmelding',
		checked: has('inntektsmelding'),
		add: () =>
			set('inntektsmelding', {
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
			}),
		remove: () => del('inntektsmelding'),
	},
	inntektstub: {
		label: 'Har inntekt',
		checked: has('inntektstub'),
		add: () =>
			set('inntektstub', {
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
			}),
		remove() {
			del('inntektstub')
		},
	},
	skattekort: {
		label: 'Har skattekort',
		checked: has('skattekort'),
		add: () =>
			set('skattekort', {
				arbeidsgiverSkatt: [initialArbeidsgiverSkatt],
			}),
		remove: () => del('skattekort'),
	},
})
