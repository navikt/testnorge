import { AdresseVelger } from '@/components/adresseVelger'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import LoadableComponent from '@/components/ui/loading/LoadableComponent'
import { DollyApi } from '@/service/Api'
import { AdresseKodeverk } from '@/config/kodeverk'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import * as _ from 'lodash-es'
import styled from 'styled-components'
import { Adresse } from '@/components/adresseVelger/AdresseVelger'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface VegadresseValues {
	formMethods: UseFormReturn
	path: string
}

type Postnummer = {
	koder: [
		{
			value: string
			label: string
		},
	]
}

const StyledVegadresse = styled.div`
	width: 100%;
`

export const Vegadresse = ({ formMethods, path }: VegadresseValues) => {
	const settVegadresse = (adresse: Adresse) => {
		formMethods.setValue(path, {
			postnummer: adresse.postnummer,
			adressenavn: adresse.adressenavn,
			adressekode: adresse.adressekode,
			tilleggsnavn: adresse.tilleggsnavn,
			husnummer: adresse.husnummer,
			husbokstav: adresse.husbokstav,
			kommunenummer: adresse.kommunenummer,
			bruksenhetsnummer: adresse.bruksenhetsnummer,
			vegadresseType: _.get(formMethods.getValues(), `${path}.vegadresseType`),
		})
	}

	const renderAdresse = (postnummerListe: Postnummer) => {
		const { adressenavn, husnummer, postnummer } = _.get(formMethods.getValues(), path)
		if (!adressenavn) {
			return ''
		}
		const poststed = postnummerListe.koder.find((element) => element.value === postnummer)?.label
		return `${adressenavn} ${parseInt(husnummer)}, ${postnummer} ${poststed}`
	}

	return (
		<div className="flexbox--flex-wrap">
			<StyledVegadresse>
				<AdresseVelger onSelect={settVegadresse} />
				<ErrorBoundary>
					<LoadableComponent
						onFetch={() => DollyApi.getKodeverkByNavn(AdresseKodeverk.PostnummerUtenPostboks)}
						render={(data) => (
							<DollyTextInput
								name="vegadresse"
								// @ts-ignore
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
