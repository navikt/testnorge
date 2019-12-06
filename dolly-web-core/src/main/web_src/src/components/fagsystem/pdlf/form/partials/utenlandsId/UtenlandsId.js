import React from 'react'
import { FieldArray } from 'formik'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FieldArrayAddButton, FieldArrayRemoveButton } from '~/components/ui/form/formUtils'

export const UtenlandsId = ({ formikBag }) => {
	const initialValues = formikBag.initialValues.pdlforvalter.utenlandskIdentifikasjonsnummer[0]
	return (
		<FieldArray
			name="pdlforvalter.utenlandskIdentifikasjonsnummer"
			render={arrayHelpers => (
				<React.Fragment>
					{formikBag.values.pdlforvalter.utenlandskIdentifikasjonsnummer.map((curr, idx) => (
						<div key={idx}>
							<FormikTextInput
								name={`pdlforvalter.utenlandskIdentifikasjonsnummer[${idx}].identifikasjonsnummer`}
								label="Identifikasjonsnummer"
							/>
							<FormikTextInput
								name={`pdlforvalter.utenlandskIdentifikasjonsnummer[${idx}].kilde`}
								label="Kilde"
							/>
							<FormikSelect
								name={`pdlforvalter.utenlandskIdentifikasjonsnummer[${idx}].opphoert`}
								label="Opphørt"
								options={Options('boolean')}
								isClearable={false}
								size="grow"
							/>
							<FormikSelect
								name={`pdlforvalter.utenlandskIdentifikasjonsnummer[${idx}].utstederland`}
								label="Utstederland"
								kodeverk="Landkoder"
								isClearable={false}
								size="grow"
							/>
							<FieldArrayRemoveButton onClick={e => arrayHelpers.remove(idx)} />
						</div>
					))}
					<FieldArrayAddButton
						title="Utenlandsk identifikasjonsnummer"
						onClick={e => arrayHelpers.push(initialValues)}
					/>
				</React.Fragment>
			)}
		/>
	)
}
