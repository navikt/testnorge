import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

export const MatrikkelAdresse = ({ formikBag }) => {
	return (
		<Kategori title="Matrikkeladresse">
			<FormikTextInput name="tpsf.boadresse.mellomnavn" label="Stedsnavn" />
			<FormikTextInput name="tpsf.boadresse.gardsnr" label="Gårdsnummer" type="number" />
			<FormikTextInput name="tpsf.boadresse.bruksnr" label="Bruksnummer" type="number" />
			<FormikTextInput name="tpsf.boadresse.festnr" label="Festenummer" type="number" />
			<FormikTextInput name="tpsf.boadresse.undernr" label="Undernummer" type="number" />
			<FormikSelect name="tpsf.boadresse.postnr" label="Postnummer" kodeverk="Postnummer" />
			<FormikSelect name="tpsf.boadresse.kommunenr" label="Kommunenummer" kodeverk="Kommuner" />
		</Kategori>
	)
}
