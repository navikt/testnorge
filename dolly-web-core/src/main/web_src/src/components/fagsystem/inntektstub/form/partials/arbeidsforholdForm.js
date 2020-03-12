import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

const initialValues = {
	arbeidsforholdstype: '',
	startdato: undefined,
	sluttdato: undefined,
	antallTimerPerUkeSomEnFullStillingTilsvarer: '',
	avloenningstype: '',
	yrke: '',
	arbeidstidsordning: '',
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
						kodeverk="Arbeidsforholdstyper"
						size="medium"
					/>
					<FormikDatepicker name={`${path}.startdato`} label="Startdato" />
					<FormikDatepicker name={`${path}.sluttdato`} label="Sluttdato" />
					<FormikTextInput
						name={`${path}.antallTimerPerUkeSomEnFullStillingTilsvarer`}
						label="Timer per uke (full stilling)"
						type="number"
					/>
					<FormikSelect
						name={`${path}.avloenningstype`}
						label="Avlønningstype"
						kodeverk="Avlønningstyper"
						size="medium"
					/>
					<FormikSelect name={`${path}.yrke`} label="Yrke" kodeverk="Yrker" size="large" />
					<FormikSelect
						name={`${path}.arbeidstidsordning`}
						label="Arbeidstidsordning"
						kodeverk="Arbeidstidsordninger"
						size="medium"
					/>
					<FormikTextInput
						name={`${path}.stillingsprosent`}
						label="Stillingsprosent"
						type="number"
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
