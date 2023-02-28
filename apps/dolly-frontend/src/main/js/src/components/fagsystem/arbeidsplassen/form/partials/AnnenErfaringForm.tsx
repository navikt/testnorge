import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialAnnenErfaring } from '@/components/fagsystem/arbeidsplassen/form/initialValues'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { Fritekstfelt } from '@/components/fagsystem/arbeidsplassen/form/styles'
import _get from 'lodash/get'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as React from 'react'

export const AnnenErfaringForm = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="arbeidsplassenCV.annenErfaring"
			header="Annen erfaring"
			// hjelpetekst={infotekst}
			newEntry={initialAnnenErfaring}
			nested
		>
			{(annenErfaringPath, idx) => (
				<div key={idx} className="flexbox--flex-wrap">
					<FormikTextInput name={`${annenErfaringPath}.role`} label="Rolle" size="xlarge" />
					<Fritekstfelt
						label="Beskrivelse"
						placeholder="Beskrivelse av annen erfaring"
						value={_get(formikBag.values, `${annenErfaringPath}.description`)}
						onChange={(beskrivelse) =>
							formikBag.setFieldValue(
								`${annenErfaringPath}.description`,
								beskrivelse?.target?.value
							)
						}
						size="small"
					/>
					<FormikDatepicker name={`${annenErfaringPath}.fromDate`} label="Startdato" />
					<FormikDatepicker
						name={`${annenErfaringPath}.toDate`}
						label="Sluttdato"
						disabled={_get(formikBag.values, `${annenErfaringPath}.ongoing`)}
						fastfield={false}
					/>
					<FormikCheckbox
						name={`${annenErfaringPath}.ongoing`}
						label="Pågående"
						wrapperSize="inherit"
						isDisabled={_get(formikBag.values, `${annenErfaringPath}.toDate`)}
						checkboxMargin
					/>
				</div>
			)}
		</FormikDollyFieldArray>
	)
}
