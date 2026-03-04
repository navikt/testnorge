import React from 'react'
// @ts-ignore
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '@/config/kodeverk'
import { LandVelger } from '@/components/landVelger/LandVelger'
import { UseFormReturn } from 'react-hook-form/dist/types'

type UtvandringTypes = {
	path: string
	formMethods: UseFormReturn
	minDate?: Date
	maxDate?: Date
}

export const UtvandringForm = ({ path, formMethods }: UtvandringTypes) => {
	return (
		<>
			<LandVelger
				formMethods={formMethods}
				path={`${path}.tilflyttingsland`}
				checkboxName={`${path}.ukjentLand`}
				ukjentLandKode="XUK"
				label="Utvandret til"
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
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

export const Utvandring = ({ formMethods }: { formMethods: UseFormReturn }) => {
	return (
		<div className="person-visning_content">
			<UtvandringForm path={'pdldata.person.utflytting[0]'} formMethods={formMethods} />
		</div>
	)
}
