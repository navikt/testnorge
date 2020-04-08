import React from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import _has from 'lodash/has'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { requiredDate, requiredString, requiredNumber, messages } from '~/utils/YupValidations'
import { FormikProps } from 'formik'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { Kodeverk, Ytelser } from '../InntektsmeldingTypes'
import InntektsmeldingOrgnummerSelect from './partials/InntektsmeldingOrgnummerSelect'
import InntektsmeldingSelect from './partials/InntektsmeldingSelect'
import InntektsmeldingYtelseSelect from './partials/InntektsmeldingYtelseSelect'
import OmsorgspengerForm from './partials/omsorgspengerForm'
import SykepengerForm from './partials/sykepengerForm'
import PleiepengerForm from './partials/pleiepengerForm'
import RefusjonForm from './partials/refusjonForm'
import ArbeidsforholdForm from './partials/arbeidsforholdForm'
import NaturalytelseForm from './partials/naturalytelseForm'

interface InntektsmeldingForm {
	formikBag: FormikProps<{}>
}

const initialValues = {
	aarsakTilInnsending: '',
	arbeidsgiver: {
		virksomhetsnummer: '',
		kontaktinformasjon: { kontaktinformasjonNavn: 'SJÆFEN SJØL', telefonnummer: '99999999' }
	},
	arbeidsforhold: {
		beregnetInntekt: {
			beloep: ''
		},
		foersteFravaersdag: ''
	},
	avsendersystem: {
		innsendingstidspunkt: new Date(),
		systemnavn: 'Dolly',
		systemversjon: 'v1'
	},
	refusjon: {
		refusjonsbeloepPrMnd: '',
		refusjonsopphoersdato: ''
	},
	naerRelasjon: false,
	ytelse: ''
}
const inntektsmeldingAttributt = 'inntektsmelding'
const informasjonstekst = 'Personen må ha et arbeidsforhold knyttet til den valgte virksomheten.'

export const InntektsmeldingForm = ({ formikBag }: InntektsmeldingForm) => {
	return (
		//@ts-ignore
		<Vis attributt={inntektsmeldingAttributt}>
			<Panel
				heading="Inntektsmelding (fra Altinn)"
				hasErrors={panelError(formikBag, inntektsmeldingAttributt)}
				iconType="inntektsmelding"
				informasjonstekst={informasjonstekst}
				//@ts-ignore
				startOpen={() => erForste(formikBag.values, [inntektsmeldingAttributt])}
			>
				{/* //TODO Vi må finne på en god løsning her! */}
				{!_has(formikBag.values, 'aareg') && (
					<AlertStripeInfo>
						Personen må ha et arbeidsforhold knyttet til den samme virksomheten som du velger i
						inntektsmeldingen. Gå tilbake til forrige steg og huk av for Arbeidsforhold (Aareg)
						også.
					</AlertStripeInfo>
				)}
				<FormikDollyFieldArray
					name="inntektsmelding.inntekter"
					header="Inntekt"
					newEntry={initialValues}
				>
					{(path: any, idx: number) => {
						const ytelse = _get(formikBag.values, `${path}.ytelse`)
						return (
							//TODO: Erstatte style med classNames. Styling generelt
							<div style={{ display: 'flex', flexDirection: 'column' }}>
								<div className="flexbox--flex-wrap">
									<InntektsmeldingSelect
										path={`${path}.aarsakTilInnsending`}
										label="Årsak til innsending"
										kodeverk={Kodeverk.AarsakTilInnsending}
									/>
									<InntektsmeldingYtelseSelect
										path={`${path}.ytelse`}
										idx={idx}
										label="Ytelse"
										kodeverk={Kodeverk.Ytelse}
										formikBag={formikBag}
									/>

									<InntektsmeldingOrgnummerSelect
										path={`${path}.arbeidsgiver`}
										formikBag={formikBag}
									/>
									<FormikDatepicker
										name={`${path}.avsendersystem.innsendingstidspunkt`}
										label="Innsendingstidspunkt"
									/>
									<FormikCheckbox
										name={`${path}.naerRelasjon`}
										label="Nær relasjon"
										checkboxMargin
									/>
								</div>
								<ArbeidsforholdForm path={`${path}.arbeidsforhold`} ytelse={ytelse} />
								<Kategori title="Refusjon">
									<RefusjonForm path={`${path}.refusjon`} ytelse={ytelse} />
								</Kategori>
								<Kategori title="Naturalytelse">
									<NaturalytelseForm path={`${path}`} />
								</Kategori>
								{ytelse === Ytelser.Foreldrepenger && (
									<Kategori title="Foreldrepenger">
										<FormikDatepicker
											name={`${path}.startdatoForeldrepengeperiode`}
											label="Startdato for periode"
										/>
									</Kategori>
								)}
								{ytelse === Ytelser.Sykepenger && (
									<Kategori title="Sykepenger">
										<SykepengerForm path={`${path}.sykepengerIArbeidsgiverperioden`} />
									</Kategori>
								)}
								{ytelse === Ytelser.Pleiepenger && (
									<PleiepengerForm path={`${path}.pleiepengerPerioder`} />
								)}
								{ytelse === Ytelser.Omsorgspenger && (
									<Kategori title="Omsorgspenger">
										<OmsorgspengerForm path={`${path}.omsorgspenger`} />
									</Kategori>
								)}
							</div>
						)
					}}
				</FormikDollyFieldArray>
			</Panel>
		</Vis>
	)
}

InntektsmeldingForm.validation = {
	inntektsmelding: Yup.object({
		inntekter: Yup.array().of(
			Yup.object({
				aarsakTilInnsending: requiredString,
				arbeidsgiver: Yup.object({
					virksomhetsnummer: requiredString
				}),
				arbeidsforhold: Yup.object({
					beregnetInntekt: Yup.object({
						beloep: requiredNumber.typeError(messages.required)
					})
				}),
				avsendersystem: Yup.object({
					innsendingstidspunkt: requiredDate
				}),
				ytelse: requiredString
			})
		)
	})
}
