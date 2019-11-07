import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

export const MatrikkelAdresse = ({ formikProps }) => {
	const settMatrikkeladresse = adresse => {
		// Denne bør nok settes fra initalValues i stedet
		formikProps.setFieldValue('tpsf.boadresse.adressetype', 'MATR')
	}

	return (
		<Kategori title="Matrikkeladresse">
			<FormikTextInput name="tpsf.boadresse.mellomnavn" label="Stedsnavn" />
			<FormikTextInput name="tpsf.boadresse.gardsnr" label="Gårdsnummer" />
			<FormikTextInput name="tpsf.boadresse.bruksnr" label="Bruksnummer" />
			<FormikTextInput name="tpsf.boadresse.festnr" label="Festnummer" />
			<FormikTextInput name="tpsf.boadresse.undernr" label="Undernummer" />
			<FormikTextInput name="tpsf.boadresse.postnr" label="Postnummer" />
			<FormikTextInput name="tpsf.boadresse.kommunenr" label="Kommunenummer" />
		</Kategori>
	)
}
