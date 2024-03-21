import React from 'react'
// @ts-ignore
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '@/config/kodeverk'

type UtvandringTypes = {
	path: string
	minDate?: Date
	maxDate?: Date
}

export const UtvandringForm = ({ path }: UtvandringTypes) => {
	return (
		<>
			<FormSelect
				name={`${path}.tilflyttingsland`}
				label="Utvandret til"
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				size="large"
				isClearable={false}
			/>
			<FormTextInput name={`${path}.tilflyttingsstedIUtlandet`} label="Tilflyttingssted" />
			<FormDatepicker
				name={`${path}.utflyttingsdato`}
				label="Utflyttingsdato"
				maxDate={new Date()}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Utvandring = () => {
	return (
		<div className="person-visning_content">
			<UtvandringForm path={'pdldata.person.utflytting[0]'} />
		</div>
	)
}
