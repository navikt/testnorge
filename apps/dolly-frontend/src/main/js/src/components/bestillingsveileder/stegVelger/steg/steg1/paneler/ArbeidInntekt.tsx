import Panel from '@/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialArbeidsforholdOrg } from '@/components/fagsystem/aareg/form/initialValues'
import { harValgtAttributt } from '@/components/ui/form/formUtils'
import { aaregAttributt } from '@/components/fagsystem/aareg/form/Form'
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
import {
	getInitialSummertSkattegrunnlag,
	sigrunstubSummertSkattegrunnlagAttributt,
} from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/Form'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { getTimeoutAttr } from '@/components/bestillingsveileder/utils/timeoutTitle'

export const ArbeidInntektPanel = ({ stateModifier, formValues }: any) => {
	const sm = stateModifier(ArbeidInntektPanel.initialValues)
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const infoTekst =
		'Arbeidsforhold: \nDataene her blir lagt til AAREG. \n\nInntekt: \nSkatte- og inntektsgrunnlag. Inntektene blir lagt i Sigrun-stub.' +
		'\n\nPensjonsgivende inntekt: \nInntektene blir lagt til i POPP-register. \n\nInntektstub: \nInformasjonen blir lagt i Inntekt-stub.' +
		'\n\nSkattekort: Dataene blir lagt til i SOKOS.'
	const aaregTimeout = getTimeoutAttr('AAREG', opts)
	return (
		<Panel
			heading={ArbeidInntektPanel.heading}
			informasjonstekst={infoTekst as any}
			checkAttributeArray={sm.batchAdd as any}
			uncheckAttributeArray={sm.batchRemove as any}
			iconType="arbeid"
			startOpen={harValgtAttributt(formValues, [
				aaregAttributt,
				inntektstubAttributt,
				inntektsmeldingAttributt,
				sigrunstubPensjonsgivendeAttributt,
				sigrunstubSummertSkattegrunnlagAttributt,
				skattekortAttributt,
			])}
		>
			<AttributtKategori title="Arbeidsforhold (Aareg)" attr={sm.attrs}>
				<Attributt
					attr={sm.attrs.aareg}
					data-testid="checkbox-aareg"
					disabled={aaregTimeout.disabled}
					title={aaregTimeout.title}
				/>
			</AttributtKategori>
			<AttributtKategori title="Inntekt (Sigrun)" attr={sm.attrs}>
				<div style={{ display: 'flex', flexWrap: 'wrap' }}>
					<Attributt attr={sm.attrs.sigrunstubPensjonsgivende} />
					<Attributt attr={sm.attrs.sigrunstubSummertSkattegrunnlag} />
				</div>
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

ArbeidInntektPanel.initialValues = ({ set, opts, del, has }: any) => {
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
		sigrunstubPensjonsgivende: {
			label: 'Pensjonsgivende inntekt',
			checked: has(sigrunstubPensjonsgivendeAttributt),
			add: () => set(sigrunstubPensjonsgivendeAttributt, [getInitialSigrunstubPensjonsgivende()]),
			remove: () => del(sigrunstubPensjonsgivendeAttributt),
		},
		sigrunstubSummertSkattegrunnlag: {
			label: 'Summert skattegrunnlag',
			checked: has(sigrunstubSummertSkattegrunnlagAttributt),
			add: () => set(sigrunstubSummertSkattegrunnlagAttributt, [getInitialSummertSkattegrunnlag()]),
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
