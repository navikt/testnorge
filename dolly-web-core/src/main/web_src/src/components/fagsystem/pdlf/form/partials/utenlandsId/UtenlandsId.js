import React from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

const initialValues = { identifikasjonsnummer: '', kilde: '', opphoert: '', utstederland: '' }

export const UtenlandsId = ({ formikBag }) => (
	<DollyFieldArray
		name="pdlforvalter.utenlandskIdentifikasjonsnummer"
		title="Utenlandsk ID"
		newEntry={initialValues}
	>
		{(path, idx) => (
			<React.Fragment key={idx}>
				<FormikTextInput name={`${path}.identifikasjonsnummer`} label="Identifikasjonsnummer" />
				<FormikTextInput name={`${path}.kilde`} label="Kilde" />
				<FormikSelect
					name={`${path}.opphoert`}
					label="Opphørt"
					options={Options('boolean')}
					isClearable={false}
					size="grow"
				/>
				<FormikSelect
					name={`${path}.utstederland`}
					label="Utstederland"
					kodeverk="Landkoder"
					isClearable={false}
					size="grow"
				/>
			</React.Fragment>
		)}
	</DollyFieldArray>
)
