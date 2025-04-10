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
import { hentAaregEksisterendeData } from '@/components/fagsystem/aareg/form/utils'
import { sigrunstubSummertSkattegrunnlagAttributt } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/Form'

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
			// ignoreKey 'aareg' kan fjernes naar oppretting til Aareg er tilgjengelig igjen
			checkAttributeArray={() => {
				sm.batchAdd(['aareg'])
			}}
			uncheckAttributeArray={sm.batchRemove}
			iconType="arbeid"
			startOpen={harValgtAttributt(formValues, [
				aaregAttributt,
				sigrunAttributt,
				inntektstubAttributt,
				inntektsmeldingAttributt,
				sigrunstubPensjonsgivendeAttributt,
				sigrunstubSummertSkattegrunnlagAttributt,
				skattekortAttributt,
			])}
		>
			{/*Valg av Aareg er foreloepig disabled fram til Team Arbeidsforhold har gjort noedvendige tilpasninger*/}
			<AttributtKategori title="Arbeidsforhold (Aareg, foreløpig utilgjengelig)" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.aareg}
					disabled={true}
					title="Oppretting til Aareg er foreløpig utilgjengelig."
				/>
			</AttributtKategori>
			<AttributtKategori title="Inntekt (Sigrun)" attr={sm.attrs}>
				<Attributt attr={sm.attrs.sigrunstub} />
				<Attributt attr={sm.attrs.sigrunstubPensjonsgivende} />
				<Attributt attr={sm.attrs.sigrunstubSummertSkattegrunnlag} />
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

ArbeidInntektPanel.heading = 'Arbeid og inntekt'

ArbeidInntektPanel.initialValues = ({ set, opts, del, has }) => {
	const eksisterendeAaregData = hentAaregEksisterendeData(opts?.personFoerLeggTil)

	return {
		aareg: {
			label: 'Har arbeidsforhold',
			checked: has('aareg'),
			add: () => set('aareg', eksisterendeAaregData || [initialArbeidsforholdOrg]),
			remove() {
				del('aareg')
			},
		},
		sigrunstub: {
			label: 'Har lignet inntekt',
			checked: has(sigrunAttributt),
			add: () =>
				set(sigrunAttributt, [
					{
						inntektsaar: new Date().getFullYear(),
						tjeneste: '',
						grunnlag: [],
						svalbardGrunnlag: [],
					},
				]),
			remove: () => del(sigrunAttributt),
		},
		sigrunstubPensjonsgivende: {
			label: 'Har pensjonsgivende inntekt',
			checked: has(sigrunstubPensjonsgivendeAttributt),
			add: () => set(sigrunstubPensjonsgivendeAttributt, [getInitialSigrunstubPensjonsgivende()]),
			remove: () => del(sigrunstubPensjonsgivendeAttributt),
		},
		sigrunstubSummertSkattegrunnlag: {
			label: 'Har summert skattegrunnlag',
			checked: has(sigrunstubSummertSkattegrunnlagAttributt),
			add: () =>
				set(sigrunstubSummertSkattegrunnlagAttributt, [getInitialSigrunstubPensjonsgivende()]),
			remove: () => del(sigrunstubSummertSkattegrunnlagAttributt),
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
					arbeidsgiverSkatt: [initialArbeidsgiverSkatt()],
				}),
			remove: () => del('skattekort'),
		},
	}
}
