import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { ArbeidKodeverk } from '@/config/kodeverk'
import React from 'react'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { useArbeidsforholdstyperInntektstub } from '@/utils/hooks/useInntektstub'

const initialValues = {
	arbeidsforholdstype: '',
	startdato: undefined,
	sluttdato: undefined,
	antallTimerPerUkeSomEnFullStillingTilsvarer: '',
	avloenningstype: undefined,
	yrke: undefined,
	arbeidstidsordning: undefined,
	stillingsprosent: '',
	sisteLoennsendringsdato: undefined,
	sisteDatoForStillingsprosentendring: undefined,
}

export const ArbeidsforholdForm = ({ formMethods, inntektsinformasjonPath }) => {
	const { arbeidsforholdstyper, loading } = useArbeidsforholdstyperInntektstub()
	const arbeidsforholdstyperFormatted = SelectOptionsFormat.formatOptions(
		'arbeidsforholdstyper',
		arbeidsforholdstyper,
	)

	return (
		<FormDollyFieldArray
			name={`${inntektsinformasjonPath}.arbeidsforholdsliste`}
			header="Arbeidsforhold"
			newEntry={initialValues}
		>
			{(path) => (
				<React.Fragment>
					<DollySelect
						name={`${path}.arbeidsforholdstype`}
						label="Arbeidsforholdstype"
						options={arbeidsforholdstyperFormatted}
						isLoading={loading}
						onChange={(forhold) =>
							formMethods.setValue(`${path}.arbeidsforholdstype`, forhold.value)
						}
						value={formMethods.watch(`${path}.arbeidsforholdstype`)}
						size="xlarge"
						isClearable={false}
					/>

					<FormDatepicker name={`${path}.startdato`} label="Startdato" />
					<FormDatepicker name={`${path}.sluttdato`} label="Sluttdato" />
					<FormTextInput
						name={`${path}.antallTimerPerUkeSomEnFullStillingTilsvarer`}
						label="Timer per uke (full stilling)"
						type="number"
					/>
					<FormTextInput name={`${path}.stillingsprosent`} label="Stillingsprosent" type="number" />
					<FormSelect
						name={`${path}.avloenningstype`}
						label="Avlønningstype"
						kodeverk={ArbeidKodeverk.Avloenningstyper}
						size="medium"
					/>
					<FormSelect
						name={`${path}.yrke`}
						label="Yrke"
						kodeverk={ArbeidKodeverk.Yrker}
						size="xxxlarge"
						optionHeight={50}
					/>
					<FormSelect
						name={`${path}.arbeidstidsordning`}
						label="Arbeidstidsordning"
						kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
						size="xxlarge"
					/>

					<FormDatepicker
						name={`${path}.sisteLoennsendringsdato`}
						label="Siste lønnsendringsdato"
					/>
					<FormDatepicker
						name={`${path}.sisteDatoForStillingsprosentendring`}
						label="Stillingsprosentendring"
					/>
				</React.Fragment>
			)}
		</FormDollyFieldArray>
	)
}
