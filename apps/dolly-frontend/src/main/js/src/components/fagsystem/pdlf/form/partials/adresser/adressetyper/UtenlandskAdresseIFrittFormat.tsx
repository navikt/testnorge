import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { GtKodeverk } from '@/config/kodeverk'
import { FrittFormatAdresse } from './FrittFormatAdresse'
import { LandVelger } from '@/components/landVelger/LandVelger'
import React from 'react'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface UtenlandskAdresseIFrittFormatValues {
	path: string
	formMethods: UseFormReturn
}

export const UtenlandskAdresseIFrittFormat = ({
	path,
	formMethods,
}: UtenlandskAdresseIFrittFormatValues) => {
	return (
		<FrittFormatAdresse
			path={path}
			extraFields={(p: string) => (
				<>
					<FormTextInput name={`${p}.postkode`} label="Postkode" />
					<FormTextInput name={`${p}.byEllerStedsnavn`} label="By eller sted" />
					<LandVelger
						formMethods={formMethods}
						path={`${p}.landkode`}
						checkboxName={`${p}.ukjentLand`}
						ukjentLandKode="XUK"
						label="Land"
						kodeverk={GtKodeverk.LAND}
					/>
				</>
			)}
		/>
	)
}
