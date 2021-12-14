import React from 'react'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'
import { initialUtenlandskAdresse } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'

interface UtenlandskAdresseForm {
	formikBag: FormikProps<{}>
	path: string
}

export const UtenlandskAdresse = ({ formikBag, path }: UtenlandskAdresseForm) => {
	const harAdressenavn =
		_get(formikBag.values, `${path}.adressenavnNummer`) !== '' &&
		_get(formikBag.values, `${path}.adressenavnNummer`) !== null
	const harPostboksnummer =
		_get(formikBag.values, `${path}.postboksNummerNavn`) !== '' &&
		_get(formikBag.values, `${path}.postboksNummerNavn`) !== null
	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput
				name={`${path}.adressenavnNummer`}
				label="Gatenavn og husnummer"
				// @ts-ignore
				disabled={harPostboksnummer}
			/>
			<FormikTextInput
				name={`${path}.postboksNummerNavn`}
				label="Postboksnummer og -navn"
				// @ts-ignore
				disabled={harAdressenavn}
			/>
			<FormikTextInput name={`${path}.postkode`} label="Postkode" />
			<FormikTextInput name={`${path}.bySted`} label="By eller sted" />
			<FormikSelect
				name={`${path}.landkode`}
				label="Land"
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				isClearable={false}
				size="large"
			/>
			<FormikTextInput name={`${path}.bygningEtasjeLeilighet`} label="Bygg-/leilighetsinfo" />
			<FormikTextInput name={`${path}.regionDistriktOmraade`} label="Region/distrikt/område" />
		</div>
	)
}
