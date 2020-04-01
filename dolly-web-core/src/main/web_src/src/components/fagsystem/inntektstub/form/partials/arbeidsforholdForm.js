import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '~/config/kodeverk'

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
	sisteDatoForStillingsprosentendring: undefined
}

export const ArbeidsforholdForm = ({ formikBag, inntektsinformasjonPath }) => {
	return (
		<FormikDollyFieldArray
			name={`${inntektsinformasjonPath}.arbeidsforholdsliste`}
			header="Arbeidsforhold"
			newEntry={initialValues}
		>
			{path => (
				<React.Fragment>
					<FormikSelect
						name={`${path}.arbeidsforholdstype`}
						label="Arbeidsforholdstype"
						kodeverk={ArbeidKodeverk.Arbeidsforholdstyper}
						size="medium"
						isClearable={false}
					/>
					<FormikDatepicker name={`${path}.startdato`} label="Startdato" />
					<FormikDatepicker name={`${path}.sluttdato`} label="Sluttdato" />
					<FormikTextInput
						name={`${path}.antallTimerPerUkeSomEnFullStillingTilsvarer`}
						label="Timer per uke (full stilling)"
						type="number"
					/>
					<FormikTextInput
						name={`${path}.stillingsprosent`}
						label="Stillingsprosent"
						type="number"
					/>
					<FormikSelect
						name={`${path}.avloenningstype`}
						label="Avlønningstype"
						kodeverk={ArbeidKodeverk.Avloenningstyper}
						size="medium"
					/>
					<FormikSelect
						name={`${path}.yrke`}
						label="Yrke"
						kodeverk={ArbeidKodeverk.Yrker}
						size="xxlarge"
						optionHeight={50}
					/>
					<FormikSelect
						name={`${path}.arbeidstidsordning`}
						label="Arbeidstidsordning"
						kodeverk={ArbeidKodeverk.Arbeidstidsordninger}
						size="xxlarge"
					/>

					<FormikDatepicker
						name={`${path}.sisteLoennsendringsdato`}
						label="Siste lønnsendringsdato"
					/>
					<FormikDatepicker
						name={`${path}.sisteDatoForStillingsprosentendring`}
						label="Stillingsprosentendring"
					/>
				</React.Fragment>
			)}
		</FormikDollyFieldArray>
	)
}
