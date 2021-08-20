import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import '../boadresse/Boadresse.less'
import { MatrikkelAdresseVelger } from '~/components/adresseVelger'
import { FormikProps } from 'formik'
import { MatrikkelAdresse } from '~/service/services/AdresseService'

type AdresseProps = {
	formikBag: FormikProps<{}>
}

export const TilfeldigMatrikkelAdresse = ({ formikBag }: AdresseProps) => {
	const settMatrikkelAdresse = (adresse: MatrikkelAdresse) => {
		formikBag.setFieldValue('tpsf.boadresse', {
			// @ts-ignore
			bolignr: formikBag.values.tpsf.boadresse.bolignr,
			// @ts-ignore
			flyttedato: formikBag.values.tpsf.boadresse.flyttedato,
			// @ts-ignore
			tilleggsadresse: formikBag.values.tpsf.boadresse.tilleggsadresse,
			matrikkelId: adresse.matrikkelId,
			adressetype: 'MATR',
			mellomnavn: adresse.tilleggsnavn,
			gardsnr: adresse.gaardsnummer,
			bruksnr: adresse.bruksnummer,
			festnr: '',
			undernr: '',
			postnr: adresse.postnummer,
			poststed: adresse.poststed,
			kommunenr: adresse.kommunenummer
		})
	}

	return (
		<Kategori title="Matrikkeladressse">
			<div className="gateadresse">
				<MatrikkelAdresseVelger onSelect={settMatrikkelAdresse} />
			</div>
		</Kategori>
	)
}
