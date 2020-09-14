import React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { FormikProps } from 'formik'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '~/config/kodeverk'
import { GyldigAdresseVelger } from '../boadresse/GyldigAdresseVelger/GyldigAdresseVelger'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollyApi } from '~/service/Api'
import { ErrorComponent } from '~/components/ui/loading/ErrorComponent'

interface GateadresseDetaljert {
	formikBag: FormikProps<{}>
}

interface Adresse {
	adressetype: string
	gateadresse: string
	gatekode: string
	husnummer: string
	kommunenr: string
	postnr: string
	poststed: string
}

export const GateadresseDetaljert = ({ formikBag }: GateadresseDetaljert) => {
	const norskAdresse = 'tpsf.midlertidigAdresse.norskAdresse'

	const settAdresse = (adresse: Adresse) => {
		formikBag.setFieldValue(`${norskAdresse}.postnr`, adresse.postnr)
		formikBag.setFieldValue(`${norskAdresse}.poststed`, adresse.poststed)
		formikBag.setFieldValue(`${norskAdresse}.gatenavn`, adresse.gateadresse)
		formikBag.setFieldValue(`${norskAdresse}.gatekode`, adresse.gatekode)
		formikBag.setFieldValue(`${norskAdresse}.husnr`, adresse.husnummer)
	}

	const renderGateadresse = () => {
		const { gatenavn, husnr, postnr, poststed } = _get(formikBag.values, norskAdresse)
		if (!gatenavn) return ''
		return `${gatenavn} ${parseInt(husnr)}, ${postnr} ${poststed}`
	}
	const feilmelding = () => {
		if (
			!_get(formikBag.values, `${norskAdresse}.gatenavn`) &&
			_has(formikBag.touched, `${norskAdresse}.gatenavn`)
		) {
			return {
				feilmelding: _get(formikBag.errors, `${norskAdresse}.gatenavn`)
			}
		}
	}

	return (
		<Kategori title="Midlertidig gateadresse">
			<div className="gateadresse">
				<GyldigAdresseVelger settBoadresse={settAdresse} formikBag={formikBag} />
				<LoadableComponent
					onFetch={() => DollyApi.getKodeverkByNavn(AdresseKodeverk.PostnummerUtenPostboks)}
					renderOnError={error => {
						return <ErrorComponent errorMessage={error} feilKomponent={'GateadresseDetaljert'} />
					}}
					render={() => {
						return (
							<DollyTextInput
								name="boadresse"
								size="grow"
								value={renderGateadresse()}
								label="Boadresse"
								readOnly
								placeholder="Ingen valgt adresse"
								title="Endre adressen i adressevelgeren over"
								feil={feilmelding()}
							/>
						)
					}}
				/>
			</div>
		</Kategori>
	)
}
