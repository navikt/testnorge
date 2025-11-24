import { MatrikkelAdresseVelger } from '@/components/adresseVelger'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import * as _ from 'lodash-es'
import styled from 'styled-components'
import { MatrikkelAdresse } from '@/components/adresseVelger/MatrikkelAdresseVelger'
import { UseFormReturn } from 'react-hook-form/dist/types'

const StyledMatrikkeladresse = styled.div`
	width: 100%;
`

type MatrikkeladresseProps = {
	formMethods: UseFormReturn
	path: string
}

export const MatrikkeladresseTilfeldig = ({ formMethods, path }: MatrikkeladresseProps) => {
	const settMatrikkelAdresse = (adresse: MatrikkelAdresse) => {
		formMethods.setValue(path, {
			kommunenummer: adresse.kommunenummer,
			gaardsnummer: adresse.gaardsnummer,
			bruksnummer: adresse.bruksnummer,
			postnummer: adresse.postnummer,
			bruksenhetsnummer: adresse.bruksenhetsnummer,
			tilleggsnavn: adresse.tilleggsnavn,
			matrikkeladresseType: formMethods.watch(`${path}.matrikkeladresseType`),
		})
		formMethods.trigger(path)
	}

	const renderAdresse = () => {
		const { kommunenummer, gaardsnummer, bruksnummer, postnummer, tilleggsnavn } = _.get(
			formMethods.getValues(),
			path,
		)
		if (kommunenummer) {
			return `Gårdsnr: ${gaardsnummer}, Bruksnr: ${bruksnummer}, Kommunenr: ${kommunenummer}, Postnr: ${postnummer} ${
				tilleggsnavn ? ', Tilleggsnavn: ' + tilleggsnavn : ''
			}`
		}
		return 'Ingen valgt adresse'
	}

	return (
		<div className="flexbox--flex-wrap">
			<StyledMatrikkeladresse>
				<MatrikkelAdresseVelger onSelect={settMatrikkelAdresse} />
				<DollyTextInput
					name="matrikkeladresse"
					// @ts-ignore
					size="grow"
					value={renderAdresse()}
					label="Matrikkeladresse"
					readOnly
					placeholder="Ingen valgt adresse"
					title="Endre adressen i adressevelgeren over"
				/>
			</StyledMatrikkeladresse>
		</div>
	)
}
