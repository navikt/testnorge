import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialUtdanning } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as React from 'react'

export const UtdanningForm = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="arbeidsplassenCV.utdanning"
			header="Utdanninger"
			// hjelpetekst={infotekst}
			newEntry={initialUtdanning}
			buttonText="Utdanning"
			nested
		>
			{(utdanningPath, idx) => (
				<div key={idx} className="flexbox--flex-wrap">
					<FormikTextInput name={`${utdanningPath}.nuskode`} label="Utdanningsnivå" />
					<FormikTextInput name={`${utdanningPath}.field`} label="Grad og utdanningsretning" />
					<FormikTextInput name={`${utdanningPath}.institution`} label="Skole/studiested" />
					<FormikTextInput name={`${utdanningPath}.description`} label="Beskrivelse" />
					<FormikCheckbox
						name={`${utdanningPath}.ongoing`}
						label="Nåværende utdanning"
						// isDisabled={}
						checkboxMargin
					/>
					<FormikDatepicker name={`${utdanningPath}.fromDate`} label="Startdato" />
					<FormikDatepicker name={`${utdanningPath}.toDate`} label="Sluttdato" />
				</div>
			)}
		</FormikDollyFieldArray>
	)
}
