import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialArbeidserfaring } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as React from 'react'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { Textarea } from '@navikt/ds-react'
import styled from 'styled-components'
import _get from 'lodash/get'

const Fritekstfelt = styled(Textarea)`
	width: 100%;
	margin-bottom: 1rem;
	margin-right: 20px;

	textarea {
		font-size: 1em;
	}

	&& {
		label {
			font-size: 0.75em;
			text-transform: uppercase;
			font-weight: 400;
			margin-bottom: -8px;
		}
	}
`

export const ArbeidserfaringForm = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="arbeidsplassenCV.arbeidserfaring"
			header="Arbeidserfaring"
			// hjelpetekst={infotekst}
			newEntry={initialArbeidserfaring}
			buttonText="Arbeidsforhold"
			nested
		>
			{(arbeidsforholdPath, idx) => (
				<div key={idx} className="flexbox--flex-wrap">
					{/*TODO: Må kanskje sette styrkkode også. Og finn ut hvilke data som er riktig å bruke i lista*/}
					<FormikSelect
						name={`${arbeidsforholdPath}.jobTitle`}
						label="Stilling/yrke"
						kodeverk={ArbeidKodeverk.Yrker}
						size="xxlarge"
						isClearable={false}
						optionHeight={50}
					/>
					<FormikTextInput
						name={`${arbeidsforholdPath}.alternativeJobTitle`}
						label="Alternativ tittel"
						size="large"
					/>
					<FormikTextInput name={`${arbeidsforholdPath}.employer`} label="Bedrift" size="large" />
					<FormikTextInput name={`${arbeidsforholdPath}.location`} label="Sted" size="large" />
					<Fritekstfelt
						label="Arbeidsoppgaver"
						placeholder="Beskrivelse av arbeidsoppgaver"
						autoFocus
						size="small"
					/>
					<FormikDatepicker name={`${arbeidsforholdPath}.fromDate`} label="Ansatt fra" />
					<FormikDatepicker
						name={`${arbeidsforholdPath}.toDate`}
						label="Ansatt til"
						disabled={_get(formikBag.values, `${arbeidsforholdPath}.ongoing`)}
						fastfield={false}
					/>
					<FormikCheckbox
						name={`${arbeidsforholdPath}.ongoing`}
						label="Nåværende jobb"
						wrapperSize="inherit"
						isDisabled={_get(formikBag.values, `${arbeidsforholdPath}.toDate`)}
						checkboxMargin
					/>
				</div>
			)}
		</FormikDollyFieldArray>
	)
}
