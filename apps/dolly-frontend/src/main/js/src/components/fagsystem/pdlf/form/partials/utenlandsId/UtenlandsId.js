import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

const initialValues = { identifikasjonsnummer: '', kilde: '', opphoert: '', utstederland: '' }

export const UtenlandsId = ({ formikBag }) => (
	<FormikDollyFieldArray
		name="pdldata.person.utenlandskIdentifikasjonsnummer"
		header="Utenlandsk ID"
		newEntry={initialValues}
	>
		{(path, idx) => (
			<React.Fragment key={idx}>
				<FormikTextInput name={`${path}.identifikasjonsnummer`} label="Identifikasjonsnummer" />
				{/*<FormikTextInput name={`${path}.kilde`} label="Kilde" />*/}
				<FormikSelect
					name={`${path}.opphoert`}
					label="Opphørt"
					options={Options('boolean')}
					isClearable={false}
				/>
				<FormikSelect
					name={`${path}.utstederland`}
					label="Utstederland"
					kodeverk={AdresseKodeverk.Utstederland}
					isClearable={false}
					size="large"
				/>
			</React.Fragment>
		)}
	</FormikDollyFieldArray>
)
