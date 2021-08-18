import React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { FormikProps } from 'formik'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '~/config/kodeverk'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollyApi } from '~/service/Api'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Adresse } from '~/service/services/AdresseService'
import { AdresseVelger } from '~/components/adresseVelger'

interface GateadresseDetaljert {
	formikBag: FormikProps<{}>
}

export const GateadresseDetaljert = ({ formikBag }: GateadresseDetaljert) => {
	const norskAdresse = 'tpsf.midlertidigAdresse.norskAdresse'

	const settAdresse = (adresse: Adresse) => {
		formikBag.setFieldValue(norskAdresse, {
			matrikkelId: adresse.matrikkelId,
			postnr: adresse.postnummer,
			poststed: adresse.poststed,
			gatenavn: adresse.adressenavn,
			gatekode: adresse.adressekode,
			husnr: adresse.husnummer
		})
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
				<AdresseVelger onSelect={settAdresse} />
				<ErrorBoundary>
					<LoadableComponent
						onFetch={() => DollyApi.getKodeverkByNavn(AdresseKodeverk.PostnummerUtenPostboks)}
						render={() => (
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
						)}
					/>
				</ErrorBoundary>
			</div>
		</Kategori>
	)
}
