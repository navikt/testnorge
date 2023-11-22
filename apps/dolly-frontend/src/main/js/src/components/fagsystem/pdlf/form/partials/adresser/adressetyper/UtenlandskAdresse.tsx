import { DollyTextInput, FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { GtKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import * as _ from 'lodash'
import { useEffect } from 'react'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface UtenlandskAdresseForm {
	formMethods: UseFormReturn
	path: string
	master?: string | unknown
}

export const UtenlandskAdresse = ({ formMethods, path, master }: UtenlandskAdresseForm) => {
	const harAdressenavn =
		_.get(formMethods.getValues(), `${path}.adressenavnNummer`) !== '' &&
		_.get(formMethods.getValues(), `${path}.adressenavnNummer`) !== null

	const harPostboksnummer =
		_.get(formMethods.getValues(), `${path}.postboksNummerNavn`) !== '' &&
		_.get(formMethods.getValues(), `${path}.postboksNummerNavn`) !== null

	useEffect(() => {
		if (master !== 'PDL') {
			formMethods.setValue(`${path}.bygningEtasjeLeilighet`, null)
			formMethods.setValue(`${path}.regionDistriktOmraade`, null)
		}
	}, [master])

	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput
				name={`${path}.adressenavnNummer`}
				label="Gatenavn og husnummer"
				// @ts-ignore
				isDisabled={harPostboksnummer}
			/>
			<FormikTextInput
				name={`${path}.postboksNummerNavn`}
				label="Postboksnummer og -navn"
				// @ts-ignore
				isDisabled={harAdressenavn}
			/>
			<FormikTextInput name={`${path}.postkode`} label="Postkode" />
			<FormikTextInput name={`${path}.bySted`} label="By eller sted" />
			<FormikSelect
				name={`${path}.landkode`}
				label="Land"
				kodeverk={GtKodeverk.LAND}
				isClearable={false}
				size="large"
			/>
			{master === 'PDL' ? (
				<>
					<FormikTextInput name={`${path}.bygningEtasjeLeilighet`} label="Bygg-/leilighetsinfo" />
					<FormikTextInput name={`${path}.regionDistriktOmraade`} label="Region/distrikt/område" />
				</>
			) : (
				<>
					<DollyTextInput
						name={undefined}
						label="Bygg-/leilighetsinfo"
						title={'Kan bare settes hvis master er PDL'}
						isDisabled={true}
					/>
					<DollyTextInput
						name={undefined}
						label="Region/distrikt/område"
						title={'Kan bare settes hvis master er PDL'}
						isDisabled={true}
					/>
				</>
			)}
		</div>
	)
}
