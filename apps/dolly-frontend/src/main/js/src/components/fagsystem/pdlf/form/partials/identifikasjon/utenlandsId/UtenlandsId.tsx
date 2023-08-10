import { AdresseKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { initialUtenlandsIdValues } from '@/components/fagsystem/pdlf/form/initialValues'
import React from 'react'

export const UtenlandsIdForm = ({ path, idx }) => {
	return (
		<React.Fragment key={idx}>
			<FormikTextInput name={`${path}.identifikasjonsnummer`} label="Identifikasjonsnummer" />
			<FormikSelect
				name={`${path}.utstederland`}
				label="Utstederland"
				kodeverk={AdresseKodeverk.Utstederland}
				isClearable={false}
				size="large"
			/>
			<FormikCheckbox
				name={`${path}.opphoert`}
				id={`${path}.opphoert`}
				label="Er opphørt"
				checkboxMargin
			/>
			<AvansertForm path={path} />
		</React.Fragment>
	)
}

export const UtenlandsId = () => {
	return (
		<FormikDollyFieldArray
			name="pdldata.person.utenlandskIdentifikasjonsnummer"
			header="Utenlandsk ID"
			newEntry={initialUtenlandsIdValues}
			canBeEmpty={false}
		>
			{(path: string, idx: number) => <UtenlandsIdForm path={path} idx={idx} />}
		</FormikDollyFieldArray>
	)
}
