import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import '../boadresse/Boadresse.less'
import { MatrikkelAdresseVelger } from '~/components/adresseVelger'
import { FormikProps } from 'formik'
import { MatrikkelAdresse } from '~/service/services/AdresseService'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import _get from 'lodash/get'

type AdresseProps = {
	formikBag: FormikProps<{}>
}

const renderAdresse = (formikBag: FormikProps<{}>) => {
	const adresseType = _get(formikBag.values, 'tpsf.boadresse.adressetype')
	const kommunenummer = _get(formikBag.values, 'tpsf.boadresse.kommunenr')

	if (adresseType === 'MATR' && kommunenummer) {
		const gaardsnummer = _get(formikBag.values, 'tpsf.boadresse.gardsnr')
		const bruksnummer = _get(formikBag.values, 'tpsf.boadresse.bruksnr')
		const postnummer = _get(formikBag.values, 'tpsf.boadresse.postnr')
		const poststed = _get(formikBag.values, 'tpsf.boadresse.poststed')
		const tilleggsnavn = _get(formikBag.values, 'tpsf.boadresse.mellomnavn')
		return `Gårdsnr: ${gaardsnummer}, Bruksnr: ${bruksnummer}, Kommunenr: ${kommunenummer}, ${
			tilleggsnavn ? tilleggsnavn + ',' : ''
		} ${postnummer} ${poststed}`
	}
	return 'Ingen valgt adresse'
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
			mellomnavn: adresse.tilleggsnavn ? adresse.tilleggsnavn : '',
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
				<DollyTextInput
					name="matrikkeladresse"
					// @ts-ignore
					size="grow"
					value={renderAdresse(formikBag)}
					label="Matrikkeladresse"
					readOnly
					title="Endre adressen i adressevelgeren over"
				/>
			</div>
		</Kategori>
	)
}
