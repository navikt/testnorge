import React from 'react'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'

export const Matrikkeladresse = ({ formikBag, path }) => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput name={`${path}.gaardsnummer`} label="Gårdsnummer" type="number" />
			<FormikTextInput name={`${path}.bruksnummer`} label="Bruksnummer" type="number" />
			<FormikTextInput name={`${path}.bruksenhetsnummer`} label="Bruksenhetsnummer" />
			<FormikTextInput name={`${path}.tilleggsnavn`} label="Tilleggsnavn" />
			<FormikSelect
				name={`${path}.postnummer`}
				label="Postnummer"
				kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
				size="large"
				isClearable={false}
			/>
			<FormikSelect
				name={`${path}.kommunenummer`}
				label="Kommunenummer"
				kodeverk={AdresseKodeverk.Kommunenummer}
				size="large"
				isClearable={false}
			/>
		</div>
	)
}

// bruksenhetsnummer: "H5454"
// bruksnummer: 2121
// gaardsnummer: 9878
// kommunenummer: "3039"
// postnummer: "1384"
// tilleggsnavn: "Gården"
//
// bruksenhetsnummer: "H5454"
// bruksnummer: 89
// gaardsnummer: 41
// kommunenummer: "4626"
// postnummer: "5355"
// tilleggsnavn: "VALEN"
