import React, { useState } from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import _has from 'lodash/has'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import {
	requiredDate,
	requiredString,
	requiredNumber,
	messages,
	ifPresent
} from '~/utils/YupValidations'
import { FormikProps } from 'formik'
import { Kodeverk, Ytelser, Inntekt } from '../InntektsmeldingTypes'
import InntektsmeldingSelect from './partials/InntektsmeldingSelect'
import InntektsmeldingYtelseSelect from './partials/InntektsmeldingYtelseSelect'
import OmsorgspengerForm from './partials/omsorgspengerForm'
import SykepengerForm from './partials/sykepengerForm'
import PleiepengerForm from './partials/pleiepengerForm'
import RefusjonForm from './partials/refusjonForm'
import ArbeidsforholdForm from './partials/arbeidsforholdForm'
import NaturalytelseForm from './partials/naturalytelseForm'
import { OrganisasjonMedArbeidsforholdSelect } from '~/components/organisasjonSelect'
import { AlertAaregRequired } from '~/components/ui/brukerAlert/AlertAaregRequired'
import { InputWarning } from '~/components/ui/form/inputWarning/inputWarning'

interface InntektsmeldingForm {
	formikBag: FormikProps<{}>
}

enum TypeArbeidsgiver {
	VIRKSOMHET = 'VIRKSOMHET',
	PRIVATPERSON = 'PRIVATPERSON'
}

export const initialValues = (type: string) => ({
	aarsakTilInnsending: '',
	arbeidsgiver:
		type === TypeArbeidsgiver.VIRKSOMHET
			? {
					virksomhetsnummer: ''
			  }
			: undefined,
	arbeidsgiverPrivat:
		type === TypeArbeidsgiver.PRIVATPERSON
			? {
					arbeidsgiverFnr: ''
			  }
			: undefined,
	arbeidsforhold: {
		arbeidsforholdId: '',
		beregnetInntekt: {
			beloep: ''
		},
		foersteFravaersdag: ''
	},
	avsendersystem: {
		innsendingstidspunkt: new Date()
	},
	refusjon: {
		refusjonsbeloepPrMnd: '',
		refusjonsopphoersdato: ''
	},
	naerRelasjon: false,
	ytelse: ''
})

const inntektsmeldingAttributt = 'inntektsmelding'
const informasjonstekst = 'Personen må ha et arbeidsforhold knyttet til den valgte virksomheten.'

export const InntektsmeldingForm = ({ formikBag }: InntektsmeldingForm) => {
	const [typeArbeidsgiver, setTypeArbeidsgiver] = useState(
		_get(formikBag.values, 'inntektsmelding.inntekter[0].arbeidsgiverPrivat')
			? TypeArbeidsgiver.PRIVATPERSON
			: TypeArbeidsgiver.VIRKSOMHET
	)

	const handleArbeidsgiverChange = (type: string) => {
		setTypeArbeidsgiver(type)

		_get(formikBag.values, 'inntektsmelding.inntekter').forEach((inntekt: Inntekt, idx: number) => {
			if (type === TypeArbeidsgiver.VIRKSOMHET) {
				formikBag.setFieldValue(
					`inntektsmelding.inntekter[${idx}].arbeidsgiver.virksomhetsnummer`,
					''
				)
				formikBag.setFieldValue(`inntektsmelding.inntekter[${idx}].arbeidsgiverPrivat`, undefined)
			} else if (type === TypeArbeidsgiver.PRIVATPERSON) {
				formikBag.setFieldValue(`inntektsmelding.inntekter[${idx}].arbeidsgiver`, undefined)
				formikBag.setFieldValue(
					`inntektsmelding.inntekter[${idx}].arbeidsgiverPrivat.arbeidsgiverFnr`,
					''
				)
			}
		})
	}

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
				{!_has(formikBag.values, 'aareg') && (
					<AlertAaregRequired meldingSkjema="Inntektsmeldingen" />
				)}

				<DollySelect
					name="inntektsmelding.typeArbeidsgiver"
					label="Type arbeidsgiver"
					options={[
						{ value: TypeArbeidsgiver.VIRKSOMHET, label: 'Virksomhet' },
						{ value: TypeArbeidsgiver.PRIVATPERSON, label: 'Privatperson' }
					]}
					onChange={type => handleArbeidsgiverChange(type.value)}
					value={typeArbeidsgiver}
					isClearable={false}
				/>

				<FormikDollyFieldArray
					name="inntektsmelding.inntekter"
					header="Inntekt"
					newEntry={initialValues(typeArbeidsgiver)}
					canBeEmpty={false}
				>
					{(path: string, idx: number) => {
						const ytelse = _get(formikBag.values, `${path}.ytelse`)
						return (
							<div className="flexbox--column">
								<div className="flexbox--flex-wrap">
									<InntektsmeldingSelect
										path={`${path}.aarsakTilInnsending`}
										label="Årsak til innsending"
										kodeverk={Kodeverk.AarsakTilInnsending}
									/>
									<InntektsmeldingYtelseSelect
										path={path}
										idx={idx}
										label="Ytelse"
										kodeverk={Kodeverk.Ytelse}
										formikBag={formikBag}
									/>
									{typeArbeidsgiver === TypeArbeidsgiver.VIRKSOMHET && (
										<OrganisasjonMedArbeidsforholdSelect
											path={`${path}.arbeidsgiver.virksomhetsnummer`}
											label="Arbeidsgiver (orgnr)"
											//@ts-ignore
											isClearable={false}
										/>
									)}
									{typeArbeidsgiver === TypeArbeidsgiver.PRIVATPERSON && (
										<FormikTextInput
											name={`${path}.arbeidsgiverPrivat.arbeidsgiverFnr`}
											label="Arbeidsgiver (fnr/dnr/bost)"
										/>
									)}
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
										<InputWarning
											visWarning={!_get(formikBag.values, `${path}.startdatoForeldrepengeperiode`)}
											warningText="For automatisk behandling av inntektsmelding må dette feltet fylles ut"
										>
											<FormikDatepicker
												name={`${path}.startdatoForeldrepengeperiode`}
												label="Startdato for periode"
											/>
										</InputWarning>
									</Kategori>
								)}
								{(ytelse === Ytelser.Sykepenger || ytelse === Ytelser.Omsorgspenger) && (
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
					virksomhetsnummer: ifPresent(
						'$inntektsmelding.inntekter[0].arbeidsgiver.virksomhetsnummer',
						requiredString
					)
				}),
				arbeidsgiverPrivat: Yup.object({
					arbeidsgiverFnr: ifPresent(
						'$inntektsmelding.inntekter[0].arbeidsgiverPrivat.arbeidsgiverFnr',
						requiredString
					)
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
