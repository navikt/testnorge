import React from 'react'
// @ts-ignore
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '@/config/kodeverk'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'

type UtvandringTypes = {
	path: string
	minDate?: Date
	maxDate?: Date
}

export const UtvandringForm = ({ path }: UtvandringTypes) => {
	return (
		<>
			<FormikSelect
				name={`${path}.tilflyttingsland`}
				label="Utvandret til"
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
				size="large"
				isClearable={false}
			/>
			<FormikTextInput name={`${path}.tilflyttingsstedIUtlandet`} label="Tilflyttingssted" />
			<DatepickerWrapper>
				<FormikDatepicker
					name={`${path}.utflyttingsdato`}
					label="Utflyttingsdato"
					maxDate={new Date()}
				/>
			</DatepickerWrapper>
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
