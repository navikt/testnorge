import React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '~/config/kodeverk'
import './Boadresse.less'
import { DollyApi } from '~/service/Api'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { AdresseVelger } from '~/components/adresseVelger'

export const Boadresse = ({ formikBag }) => {
	const settBoadresse = adresse => {
		formikBag.setFieldValue('tpsf.boadresse', {
			bolignr: formikBag.values.tpsf.boadresse.bolignr,
			flyttedato: formikBag.values.tpsf.boadresse.flyttedato,
			tilleggsadresse: formikBag.values.tpsf.boadresse.tilleggsadresse,
			adressetype: 'GATE',
			matrikkelId: adresse.matrikkelId,
			postnr: adresse.postnummer,
			poststed: adresse.poststed,
			gateadresse: adresse.adressenavn,
			gatekode: adresse.adressekode,
			husnummer: adresse.husnummer,
			kommunenr: adresse.kommunenummer
		})
	}

	const renderAdresse = postnummerListe => {
		const { gateadresse, husnummer, postnr } = formikBag.values.tpsf.boadresse
		let poststed = formikBag.values.tpsf.boadresse.poststed
		if (!gateadresse) return ''

		if (postnummerListe.koder && postnr && !poststed) {
			for (let i = 0; i < postnummerListe.koder.length; i++) {
				if (postnummerListe.koder[i].value === postnr) {
					poststed = postnummerListe.koder[i].label
					formikBag.setFieldValue('tpsf.boadresse.poststed', poststed)
					break
				}
			}
		}

		return `${gateadresse} ${parseInt(husnummer)}, ${postnr} ${poststed}`
	}

	const feilmelding = feil => {
		if (
			!_get(formikBag.values, 'tpsf.boadresse.gateadresse') &&
			_has(formikBag.touched, 'tpsf.boadresse.gateadresse')
		) {
			return { feilmelding: _get(formikBag.errors, 'tpsf.boadresse.gateadresse') }
		} else return feil
	}
	return (
		<Kategori title="Gateadresse">
			<div className="gateadresse">
				<AdresseVelger onSelect={settBoadresse} formikBag={formikBag} />
				<ErrorBoundary>
					<LoadableComponent
						onFetch={() => DollyApi.getKodeverkByNavn(AdresseKodeverk.PostnummerUtenPostboks)}
						render={(data, feil) => (
							<DollyTextInput
								name="boadresse"
								size="grow"
								value={renderAdresse(data.data)}
								label="Boadresse"
								readOnly
								placeholder="Ingen valgt adresse"
								title="Endre adressen i adressevelgeren over"
								feil={feilmelding(feil)}
							/>
						)}
					/>
				</ErrorBoundary>
			</div>
		</Kategori>
	)
}
