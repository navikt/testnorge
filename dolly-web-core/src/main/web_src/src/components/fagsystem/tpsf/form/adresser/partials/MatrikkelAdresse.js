import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

export const MatrikkelAdresse = ({ formikBag }) => {
	return (
		<Kategori title="Matrikkeladresse">
			<FormikTextInput name="tpsf.boadresse.mellomnavn" label="Stedsnavn" />
			<FormikTextInput
				name="tpsf.boadresse.gardsnr"
				min="1"
				max="99999"
				label="Gårdsnummer"
				type="number"
			/>
			<FormikTextInput
				name="tpsf.boadresse.bruksnr"
				min="1"
				max="9999"
				label="Bruksnummer"
				type="number"
			/>
			<FormikTextInput
				name="tpsf.boadresse.festnr"
				min="1"
				max="9999"
				label="Festenummer"
				type="number"
			/>
			<FormikTextInput
				name="tpsf.boadresse.undernr"
				min="1"
				max="999"
				label="Undernummer"
				type="number"
			/>
			<FormikSelect name="tpsf.boadresse.postnr" label="Postnummer" kodeverk="Postnummer" />
			<FormikSelect name="tpsf.boadresse.kommunenr" label="Kommunenummer" kodeverk="Kommuner" />
		</Kategori>
	)
}
