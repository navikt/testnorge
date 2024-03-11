import React from 'react'
// @ts-ignore
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '@/config/kodeverk'

type InnvandringTypes = {
	path: string
	minDate?: Date
	maxDate?: Date
}

export const InnvandringForm = ({ path }: InnvandringTypes) => {
	return (
		<>
			<FormSelect
				name={`${path}.fraflyttingsland`}
				label="Innvandret fra"
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				size="large"
				isClearable={false}
			/>
			<FormTextInput name={`${path}.fraflyttingsstedIUtlandet`} label="Fraflyttingssted" />
			<FormDatepicker
				name={`${path}.innflyttingsdato`}
				label="Innflyttingsdato"
				maxDate={new Date()}
			/>
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const Innvandring = () => {
	return (
		<div className="person-visning_content">
			<InnvandringForm path={'pdldata.person.innflytting[0]'} />
		</div>
	)
}
