import React from 'react'
import { MatrikkelAdresseVelger } from '~/components/adresseVelger'
import { FormikProps } from 'formik'
import { MatrikkelAdresse } from '~/service/services/AdresseService'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import _get from 'lodash/get'
import styled from 'styled-components'

const StyledMatrikkeladresse = styled.div`
	width: 100%;
`

type MatrikkeladresseProps = {
	formikBag: FormikProps<{}>
	path: string
}

export const MatrikkeladresseTilfeldig = ({ formikBag, path }: MatrikkeladresseProps) => {
	const settMatrikkelAdresse = (adresse: MatrikkelAdresse) => {
		formikBag.setFieldValue(path, {
			kommunenummer: adresse.kommunenummer,
			gaardsnummer: adresse.gaardsnummer,
			bruksnummer: adresse.bruksnummer,
			postnummer: adresse.postnummer,
			bruksenhetsnummer: adresse.bruksenhetsnummer,
			tilleggsnavn: adresse.tilleggsnavn,
			matrikkeladresseType: _get(formikBag.values, `${path}.matrikkeladresseType`),
		})
	}

	const renderAdresse = () => {
		const { kommunenummer, gaardsnummer, bruksnummer, postnummer, tilleggsnavn } = _get(
			formikBag.values,
			path
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
