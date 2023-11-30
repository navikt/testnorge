import { AdresseKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { getInitialUtenlandskIdentifikasjonsnummer } from '@/components/fagsystem/pdlf/form/initialValues'
import React, { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

export const UtenlandsIdForm = ({ path, idx, identtype }) => {
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
			<AvansertForm path={path} kanVelgeMaster={identtype !== 'NPID'} />
		</React.Fragment>
	)
}

export const UtenlandsId = () => {
	const opts = useContext(BestillingsveilederContext)

	return (
		<FormikDollyFieldArray
			name="pdldata.person.utenlandskIdentifikasjonsnummer"
			header="Utenlandsk ID"
			newEntry={getInitialUtenlandskIdentifikasjonsnummer(
				opts?.identtype === 'NPID' ? 'PDL' : 'FREG',
			)}
			canBeEmpty={false}
		>
			{(path: string, idx: number) => (
				<UtenlandsIdForm path={path} idx={idx} identtype={opts?.identtype} />
			)}
		</FormikDollyFieldArray>
	)
}
