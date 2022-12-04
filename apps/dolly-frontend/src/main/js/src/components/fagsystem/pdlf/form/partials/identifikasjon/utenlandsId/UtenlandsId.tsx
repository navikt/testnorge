import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { initialUtenlandsIdValues } from '~/components/fagsystem/pdlf/form/initialValues'

export const UtenlandsId = () => (
	<FormikDollyFieldArray
		name="pdldata.person.utenlandskIdentifikasjonsnummer"
		header="Utenlandsk ID"
		newEntry={initialUtenlandsIdValues}
		canBeEmpty={false}
	>
		{(path, idx) => (
			<React.Fragment key={idx}>
				<FormikTextInput name={`${path}.identifikasjonsnummer`} label="Identifikasjonsnummer" />
				<FormikSelect
					name={`${path}.utstederland`}
					label="Utstederland"
					kodeverk={AdresseKodeverk.Utstederland}
					isClearable={false}
					size="large"
				/>
				<FormikCheckbox name={`${path}.opphoert`} label="Er opphørt" checkboxMargin />
				<AvansertForm path={path} />
			</React.Fragment>
		)}
	</FormikDollyFieldArray>
)
