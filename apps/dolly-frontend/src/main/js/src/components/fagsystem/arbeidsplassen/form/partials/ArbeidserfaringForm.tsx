import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialArbeidserfaring,
	initialArbeidserfaringVerdier,
} from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as React from 'react'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { EraseFillButtons } from '@/components/fagsystem/arbeidsplassen/form/partials/EraseFillButtons'

export const ArbeidserfaringForm = ({ formMethods }) => {
	const setYrke = (valg, path) => {
		formikBag.setFieldValue(`${path}.styrkkode`, valg.value)
		formikBag.setFieldValue(`${path}.jobTitle`, valg.label)
	}

	const arbeidserfaringListePath = 'arbeidsplassenCV.arbeidserfaring'

	return (
		<Vis attributt={arbeidserfaringListePath}>
			<FormikDollyFieldArray
				name={arbeidserfaringListePath}
				header="Arbeidserfaring"
				newEntry={initialArbeidserfaringVerdier}
				buttonText="Arbeidsforhold"
				nested
			>
				{(arbeidsforholdPath, idx) => (
					<>
						<div key={idx} className="flexbox--flex-wrap">
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
								key={`alternativeJobTitle_${_get(
									formikBag.values,
									`${arbeidsforholdPath}.alternativeJobTitle`,
								)}`}
							/>
							<FormikTextInput
								name={`${arbeidsforholdPath}.employer`}
								label="Bedrift"
								size="large"
								key={`employer_${_get(formikBag.values, `${arbeidsforholdPath}.employer`)}`}
							/>
							<FormikTextInput
								name={`${arbeidsforholdPath}.location`}
								label="Sted"
								size="large"
								key={`location_${_get(formikBag.values, `${arbeidsforholdPath}.location`)}`}
							/>
							<Fritekstfelt
								label="Arbeidsoppgaver"
								placeholder="Beskrivelse av arbeidsoppgaver"
								defaultValue={_get(formikBag.values, `${arbeidsforholdPath}.description`)}
								onBlur={(beskrivelse) =>
									formikBag.setFieldValue(
										`${arbeidsforholdPath}.description`,
										beskrivelse?.target?.value,
									)
								}
								size="small"
								key={`description_${_get(formikBag.values, `${arbeidsforholdPath}.description`)}`}
								resize
							/>
							<FormikDatepicker name={`${arbeidsforholdPath}.fromDate`} label="Ansatt fra" />
							<FormikDatepicker
								name={`${arbeidsforholdPath}.toDate`}
								label="Ansatt til"
								disabled={_get(formikBag.values, `${arbeidsforholdPath}.ongoing`)}
								fastfield={false}
							/>
							<FormikCheckbox
								id={`${arbeidsforholdPath}.ongoing`}
								name={`${arbeidsforholdPath}.ongoing`}
								label="Nåværende jobb"
								wrapperSize="inherit"
								isDisabled={_get(formikBag.values, `${arbeidsforholdPath}.toDate`)}
								checkboxMargin
							/>
						</div>
						<EraseFillButtons
							formMethods={formMethods}
							path={arbeidsforholdPath}
							initialErase={initialArbeidserfaring}
							initialFill={initialArbeidserfaringVerdier}
						/>
					</>
				)}
			</FormikDollyFieldArray>
		</Vis>
	)
}
