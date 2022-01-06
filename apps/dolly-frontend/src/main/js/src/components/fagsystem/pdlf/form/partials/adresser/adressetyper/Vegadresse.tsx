import React from 'react'
import { AdresseVelger } from '~/components/adresseVelger'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { DollyApi } from '~/service/Api'
import { AdresseKodeverk } from '~/config/kodeverk'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import _get from 'lodash/get'
import styled from 'styled-components'

const StyledVegadresse = styled.div`
	width: 100%;
`

export const Vegadresse = ({ formikBag, path }) => {
	const settVegadresse = (adresse) => {
		formikBag.setFieldValue(path, {
			postnummer: adresse.postnummer,
			adressenavn: adresse.adressenavn,
			adressekode: adresse.adressekode,
			tilleggsnavn: adresse.tilleggsnavn,
			husnummer: adresse.husnummer,
			husbokstav: adresse.husbokstav,
			kommunenummer: adresse.kommunenummer,
			bruksenhetsnummer: adresse.bruksenhetsnummer,
			vegadresseType: _get(formikBag.values, `${path}.vegadresseType`),
		})
	}

	const renderAdresse = (postnummerListe) => {
		const { adressenavn, husnummer, postnummer } = _get(formikBag.values, path)
		if (!adressenavn) return ''
		const poststed = postnummerListe.koder.find((element) => element.value === postnummer)?.label
		return `${adressenavn} ${parseInt(husnummer)}, ${postnummer} ${poststed}`
	}

	return (
		<div className="flexbox--flex-wrap">
			<StyledVegadresse>
				<AdresseVelger onSelect={settVegadresse} formikBag={formikBag} />
				<ErrorBoundary>
					<LoadableComponent
						onFetch={() => DollyApi.getKodeverkByNavn(AdresseKodeverk.PostnummerUtenPostboks)}
						render={(data, feil) => (
							<DollyTextInput
								name="vegadresse"
								size="grow"
								value={renderAdresse(data.data)}
								label="Vegadresse"
								readOnly
								placeholder="Ingen valgt adresse"
								title="Endre adressen i adressevelgeren over"
							/>
						)}
					/>
				</ErrorBoundary>
			</StyledVegadresse>
		</div>
	)
}
