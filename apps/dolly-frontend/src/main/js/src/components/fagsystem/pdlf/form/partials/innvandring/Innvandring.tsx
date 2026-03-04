import React from 'react'
// @ts-ignore
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '@/config/kodeverk'
import { LandVelger } from '@/components/landVelger/LandVelger'
import { UseFormReturn } from 'react-hook-form/dist/types'

type InnvandringTypes = {
	path: string
	formMethods: UseFormReturn
	minDate?: Date
	maxDate?: Date
}

export const InnvandringForm = ({ path, formMethods }: InnvandringTypes) => {
	return (
		<>
			<LandVelger
				formMethods={formMethods}
				path={`${path}.fraflyttingsland`}
				checkboxName={`${path}.ukjentLand`}
				ukjentLandKode="XUK"
				label="Innvandret fra"
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
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

export const Innvandring = ({ formMethods }: { formMethods: UseFormReturn }) => {
	return (
		<div className="person-visning_content">
			<InnvandringForm path={'pdldata.person.innflytting[0]'} formMethods={formMethods} />
		</div>
	)
}
