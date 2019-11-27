import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

export const AdresseNr = ({ formikBag, type }) => {
	const settAdresseNrInfo = adresse => {
		// Denne bør nok settes fra initalValues i stedet
		formikBag.setFieldValue('tpsf.boadresse.adressetype', 'MATR')
	}

	return (
		<Kategori title="Generer adresse ..">
			{type === 'postnr' && (
				<FormikSelect
					name="tpsf.adresseNrInfo.nummer"
					label="Postnummer"
					kodeverk="Postnummer"
					size="grow"
				/>
			)}
			{type === 'kommunenr' && (
				<FormikSelect
					name="tpsf.adresseNrInfo.nummer"
					label="Kommunenummer"
					kodeverk="Kommuner"
					size="grow"
				/>
			)}
		</Kategori>
	)
}
