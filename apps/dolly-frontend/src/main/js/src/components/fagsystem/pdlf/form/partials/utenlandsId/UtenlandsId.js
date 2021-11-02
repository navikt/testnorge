import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

const initialValues = {
	identifikasjonsnummer: '',
	opphoert: false,
	utstederland: '',
	kilde: 'Dolly',
	master: 'FREG',
	gjeldende: true,
}

export const UtenlandsId = ({ formikBag }) => (
	<FormikDollyFieldArray
		name="pdldata.person.utenlandskIdentifikasjonsnummer"
		header="Utenlandsk ID"
		newEntry={initialValues}
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
				<AvansertForm formikBag={formikBag} path={path} />
			</React.Fragment>
		)}
	</FormikDollyFieldArray>
)
