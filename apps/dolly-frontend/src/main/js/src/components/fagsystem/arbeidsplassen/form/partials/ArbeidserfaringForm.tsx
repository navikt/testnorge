import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialArbeidserfaring } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as React from 'react'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'

export const ArbeidserfaringForm = ({ formikBag }) => {
	const setYrke = (valg, path) => {
		formikBag.setFieldValue(`${path}.styrkkode`, valg.value)
		formikBag.setFieldValue(`${path}.jobTitle`, valg.label)
	}

	return (
		<div style={{ width: '100%' }}>
			<hr />
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
							name={`${arbeidsforholdPath}.styrkkode`}
							label="Stilling/yrke"
							options={Options('jobbYrke')}
							size="xxlarge"
							isClearable={false}
							onChange={(valg) => setYrke(valg, arbeidsforholdPath)}
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
							value={_get(formikBag.values, `${arbeidsforholdPath}.description`)}
							onChange={(beskrivelse) =>
								formikBag.setFieldValue(
									`${arbeidsforholdPath}.description`,
									beskrivelse?.target?.value
								)
							}
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
		</div>
	)
}
