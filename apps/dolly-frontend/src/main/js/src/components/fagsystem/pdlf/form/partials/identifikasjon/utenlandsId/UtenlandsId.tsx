import { AdresseKodeverk } from '@/config/kodeverk'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { getInitialUtenlandskIdentifikasjonsnummer } from '@/components/fagsystem/pdlf/form/initialValues'
import React, { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

export const UtenlandsIdForm = ({ path, idx, identtype, identMaster }) => {
	return (
		<React.Fragment key={idx}>
			<FormTextInput name={`${path}.identifikasjonsnummer`} label="Identifikasjonsnummer" />
			<FormSelect
				name={`${path}.utstederland`}
				label="Utstederland"
				kodeverk={AdresseKodeverk.Utstederland}
				isClearable={false}
				size="large"
			/>
			<FormCheckbox
				name={`${path}.opphoert`}
				id={`${path}.opphoert`}
				label="Er opphørt"
				checkboxMargin
			/>
			<AvansertForm path={path} kanVelgeMaster={identtype !== 'NPID' && identMaster !== 'PDL'} />
		</React.Fragment>
	)
}

export const UtenlandsId = () => {
	const { identtype, identMaster } = useContext(
		BestillingsveilederContext,
	) as BestillingsveilederContextType

	return (
		<FormDollyFieldArray
			name="pdldata.person.utenlandskIdentifikasjonsnummer"
			header="Utenlandsk ID"
			newEntry={getInitialUtenlandskIdentifikasjonsnummer(
				identtype === 'NPID' || identMaster === 'PDL' ? 'PDL' : 'FREG',
			)}
			canBeEmpty={false}
		>
			{(path: string, idx: number) => (
				<UtenlandsIdForm path={path} idx={idx} identtype={identtype} identMaster={identMaster} />
			)}
		</FormDollyFieldArray>
	)
}
