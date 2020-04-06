import React from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import Panel from '~/components/ui/panel/Panel'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { requiredDate, requiredString, requiredNumber, messages } from '~/utils/YupValidations'
import InntektsmeldingOrgnummerSelect from './partials/inntektsmeldingOrgnummerSelect'
import InntektsmeldingEnumSelect from './partials/inntektsmeldingEnumSelect'
import OmsorgspengerForm from './partials/omsorgspengerForm'
import SykepengerForm from './partials/sykepengerForm'
import PleiepengerForm from './partials/pleiepengerForm'
import RefusjonForm from './partials/refusjonForm'
import ArbeidsforholdForm from './partials/arbeidsforholdForm'

import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

const initialValues = {
	arbeidsgiver: {
		virksomhetsnummer: '',
		//hardkoding - husk å fjerne fra panelfil også

		kontaktinformasjon: { kontaktinformasjonNavn: 'SJÆFEN SJØL', telefonnummer: '99999999' }
	},
	arbeidsforhold: {
		beregnetInntekt: {
			beloep: ''
		}
	},
	avsendersystem: {
		innsendingstidspunkt: new Date(),
		//hardkoing - husk å fjerne fra panelfil også
		systemnavn: 'Ola incorperated sunday test',
		systemversjon: 'v3.5'
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

export const InntektsmeldingForm = ({ formikBag }) => {
	return (
		<Vis attributt={inntektsmeldingAttributt}>
			<Panel
				heading="Inntektsmelding (fra Altinn)"
				hasErrors={panelError(formikBag, inntektsmeldingAttributt)}
				iconType="inntektsmelding"
				informasjonstekst={informasjonstekst}
				startOpen={() => erForste(formikBag.values, [inntektsmeldingAttributt])}
			>
				<FormikDollyFieldArray
					name="inntektsmelding.inntekter"
					header="Inntekt"
					newEntry={initialValues}
				>
					{(path, idx) => {
						const ytelse = _get(formikBag.values, `${path}.ytelse`)
						return (
							<div className="flexbox--flex-wrap">
								<InntektsmeldingEnumSelect
									path={`${path}.ytelse`}
									label="Ytelse"
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
								<FormikCheckbox name={`${path}.naerRelasjon`} label="Nær relasjon" checkboxMargin />
								<ArbeidsforholdForm path={`${path}.arbeidsforhold`} ytelse={ytelse} />
								{/* {ytelse === 'FORELDREPENGER' && (
									<FormikDatepicker
										name={`${path}.startdatoForeldrepengeperiode`}
										label="Startdato for foreldrepengeperiode"
									/>
								)}
								{ytelse === 'SYKEPENGER' && (
									<Kategori title="Sykepenger">
										<SykepengerForm path={`${path}.sykepengerIArbeidsgiverperioden`} />
									</Kategori>
								)}
								{ytelse === 'PLEIEPENGER' && (
									<PleiepengerForm path={`${path}.pleiepengerPerioder`} />
								)} */}
								{ytelse === 'OMSORGSPENGER' && (
									<Kategori title="Omsorgspenger">
										<OmsorgspengerForm path={`${path}.omsorgspenger`} />
									</Kategori>
								)}
								<Kategori title="Refusjon">
									<RefusjonForm path={`${path}.refusjon`} />
								</Kategori>
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
