import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { GtKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import * as _ from 'lodash-es'
import { FormikProps } from 'formik'

interface UtenlandskAdresseForm {
	formikBag: FormikProps<{}>
	path: string
	master?: string | unknown
}

export const UtenlandskAdresse = ({ formikBag, path, master }: UtenlandskAdresseForm) => {
	const harAdressenavn =
		_.get(formikBag.values, `${path}.adressenavnNummer`) !== '' &&
		_.get(formikBag.values, `${path}.adressenavnNummer`) !== null

	const harPostboksnummer =
		_.get(formikBag.values, `${path}.postboksNummerNavn`) !== '' &&
		_.get(formikBag.values, `${path}.postboksNummerNavn`) !== null

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
			<FormikTextInput
				name={`${path}.bygningEtasjeLeilighet`}
				label="Bygg-/leilighetsinfo"
				isDisabled={master !== 'PDL'}
				title={master !== 'PDL' ? 'Kan bare settes hvis master er PDL' : null}
			/>
			<FormikTextInput
				name={`${path}.regionDistriktOmraade`}
				label="Region/distrikt/område"
				isDisabled={master !== 'PDL'}
				title={master !== 'PDL' ? 'Kan bare settes hvis master er PDL' : null}
			/>
		</div>
	)
}
