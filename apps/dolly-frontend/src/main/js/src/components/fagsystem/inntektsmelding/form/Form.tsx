import React, { useContext, useState } from 'react'
import * as Yup from 'yup'
import * as _ from 'lodash-es'
import Panel from '@/components/ui/panel/Panel'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import {
	ifPresent,
	messages,
	requiredDate,
	requiredNumber,
	requiredString,
} from '@/utils/YupValidations'
import { Inntekt, Kodeverk, Ytelser } from '../InntektsmeldingTypes'
import InntektsmeldingSelect from './partials/InntektsmeldingSelect'
import InntektsmeldingYtelseSelect from './partials/InntektsmeldingYtelseSelect'
import OmsorgspengerForm from './partials/omsorgspengerForm'
import SykepengerForm from './partials/sykepengerForm'
import PleiepengerForm from './partials/pleiepengerForm'
import RefusjonForm from './partials/refusjonForm'
import ArbeidsforholdForm from './partials/arbeidsforholdForm'
import NaturalytelseForm from './partials/naturalytelseForm'
import { AlertAaregRequired } from '@/components/ui/brukerAlert/AlertAaregRequired'
import { InputWarning } from '@/components/ui/form/inputWarning/inputWarning'
import { OrgnrToggle } from '@/components/fagsystem/inntektsmelding/form/partials/orgnrToogle'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'

enum TypeArbeidsgiver {
	VIRKSOMHET = 'VIRKSOMHET',
	PRIVATPERSON = 'PRIVATPERSON',
}

export const initialValues = (type: string) => ({
	aarsakTilInnsending: '',
	arbeidsgiver:
		type === TypeArbeidsgiver.VIRKSOMHET
			? {
					virksomhetsnummer: '',
				}
			: undefined,
	arbeidsgiverPrivat:
		type === TypeArbeidsgiver.PRIVATPERSON
			? {
					arbeidsgiverFnr: '',
				}
			: undefined,
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
})

export const inntektsmeldingAttributt = 'inntektsmelding'
const informasjonstekst = 'Personen må ha et arbeidsforhold knyttet til den valgte virksomheten.'

export const InntektsmeldingForm = () => {
	const formMethods = useFormContext()
	const [typeArbeidsgiver, setTypeArbeidsgiver] = useState(
		formMethods.watch('inntektsmelding.inntekter[0].arbeidsgiverPrivat')
			? TypeArbeidsgiver.PRIVATPERSON
			: TypeArbeidsgiver.VIRKSOMHET,
	)

	const { personFoerLeggTil, leggTilPaaGruppe }: any = useContext(
		BestillingsveilederContext,
	) as BestillingsveilederContextType

	const handleArbeidsgiverChange = (type: TypeArbeidsgiver) => {
		setTypeArbeidsgiver(type)

		formMethods.watch('inntektsmelding.inntekter').forEach((_inntekt: Inntekt, idx: number) => {
			if (type === TypeArbeidsgiver.VIRKSOMHET) {
				formMethods.setValue(`inntektsmelding.inntekter[${idx}].arbeidsgiver.virksomhetsnummer`, '')
				formMethods.setValue(`inntektsmelding.inntekter[${idx}].arbeidsgiverPrivat`, undefined)
			} else if (type === TypeArbeidsgiver.PRIVATPERSON) {
				formMethods.setValue(`inntektsmelding.inntekter[${idx}].arbeidsgiver`, undefined)
				formMethods.setValue(
					`inntektsmelding.inntekter[${idx}].arbeidsgiverPrivat.arbeidsgiverFnr`,
					'',
				)
			}
			formMethods.trigger(`inntektsmelding.inntekter[${idx}]`)
		})
	}

	return (
		//@ts-ignore
		<Vis attributt={inntektsmeldingAttributt}>
			<Panel
				heading="Inntektsmelding (fra Altinn)"
				hasErrors={panelError(inntektsmeldingAttributt)}
				iconType="inntektsmelding"
				informasjonstekst={informasjonstekst}
				startOpen={erForsteEllerTest(formMethods.getValues(), [inntektsmeldingAttributt])}
			>
				{!leggTilPaaGruppe &&
					!_.has(formMethods.getValues(), 'aareg') &&
					!_.has(personFoerLeggTil, 'aareg') && (
						<AlertAaregRequired meldingSkjema="Inntektsmeldingen" />
					)}

				<DollySelect
					name="inntektsmelding.typeArbeidsgiver"
					label="Type arbeidsgiver"
					options={[
						{ value: TypeArbeidsgiver.VIRKSOMHET, label: 'Virksomhet' },
						{ value: TypeArbeidsgiver.PRIVATPERSON, label: 'Privatperson' },
					]}
					onChange={(type: { value: TypeArbeidsgiver }) => handleArbeidsgiverChange(type.value)}
					value={typeArbeidsgiver}
					isClearable={false}
				/>

				<FormDollyFieldArray
					name="inntektsmelding.inntekter"
					header="Inntekt"
					newEntry={initialValues(typeArbeidsgiver)}
					canBeEmpty={false}
				>
					{(path: string, idx: number) => {
						const ytelse = formMethods.watch(`${path}.ytelse`)
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
										formMethods={formMethods}
									/>
									<FormDatepicker
										name={`${path}.avsendersystem.innsendingstidspunkt`}
										label="Innsendingstidspunkt"
									/>
									{typeArbeidsgiver === TypeArbeidsgiver.PRIVATPERSON && (
										<>
											<FormTextInput
												name={`${path}.arbeidsgiverPrivat.arbeidsgiverFnr`}
												label="Arbeidsgiver (fnr/dnr/npid)"
											/>
											<FormCheckbox
												name={`${path}.naerRelasjon`}
												id={`${path}.naerRelasjon`}
												label="Nær relasjon"
												checkboxMargin
											/>
										</>
									)}

									{typeArbeidsgiver === TypeArbeidsgiver.VIRKSOMHET && (
										<div className={'flexbox'}>
											<OrgnrToggle path={path} formMethods={formMethods} />
											<div style={{ margin: '70px 0 0 30px' }}>
												<FormCheckbox
													name={`${path}.naerRelasjon`}
													id={`${path}.naerRelasjon`}
													label="Nær relasjon"
												/>
											</div>
										</div>
									)}
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
											visWarning={!formMethods.watch(`${path}.startdatoForeldrepengeperiode`)}
											warningText="For automatisk behandling av inntektsmelding må dette feltet fylles ut"
										>
											<FormDatepicker
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
								{(ytelse === Ytelser.Pleiepenger ||
									ytelse === Ytelser.PleiepengerBarn ||
									ytelse === Ytelser.PleiepengerNaerstaaende) && (
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
				</FormDollyFieldArray>
			</Panel>
		</Vis>
	)
}

InntektsmeldingForm.validation = {
	inntektsmelding: ifPresent(
		'$inntektsmelding',
		Yup.object({
			inntekter: Yup.array().of(
				Yup.object({
					aarsakTilInnsending: requiredString,
					arbeidsgiver: Yup.object({
						virksomhetsnummer: ifPresent(
							'$inntektsmelding.inntekter[0].arbeidsgiver.virksomhetsnummer',
							requiredString.matches(/^\d{9}$/, 'Orgnummer må være et tall med 9 siffer'),
						),
					}),
					arbeidsgiverPrivat: Yup.object({
						arbeidsgiverFnr: ifPresent(
							'$inntektsmelding.inntekter[0].arbeidsgiverPrivat.arbeidsgiverFnr',
							requiredString.matches(/^\d{11}$/, 'Ident må være et tall med 11 siffer'),
						),
					}).nullable(),
					arbeidsforhold: Yup.object({
						beregnetInntekt: Yup.object({
							beloep: requiredNumber.typeError(messages.required),
						}),
						avtaltFerieListe: Yup.array().of(
							Yup.object({
								fom: testDatoFom(Yup.string(), 'tom'),
								tom: testDatoTom(Yup.string(), 'fom'),
							}),
						),
					}),
					avsendersystem: Yup.object({
						innsendingstidspunkt: requiredDate.nullable(),
					}),
					ytelse: requiredString,
				}),
			),
		}),
	),
}
