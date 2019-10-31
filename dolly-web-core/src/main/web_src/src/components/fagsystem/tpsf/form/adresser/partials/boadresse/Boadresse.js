import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { GyldigAdresseVelger } from './GyldigAdresseVelger/GyldigAdresseVelger'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const Boadresse = ({ formikProps }) => {
	const settBoadresse = adresse => {
		formikProps.setFieldValue('tpsf.boadresse.adressetype', adresse.adressetype)
		formikProps.setFieldValue('tpsf.boadresse.gateadresse', adresse.gateadresse)
		formikProps.setFieldValue('tpsf.boadresse.postnr', adresse.postnr)
		formikProps.setFieldValue('tpsf.boadresse.poststed', adresse.poststed)
		formikProps.setFieldValue('tpsf.boadresse.kommunenr', adresse.kommunenr)
		formikProps.setFieldValue('tpsf.boadresse.gatekode', adresse.gatekode)
		formikProps.setFieldValue('tpsf.boadresse.husnummer', adresse.husnummer)
	}

	const renderAdresse = () => {
		const { gateadresse, husnummer, postnr, poststed } = formikProps.values.tpsf.boadresse
		if (!gateadresse) return ''
		return `${gateadresse} ${parseInt(husnummer)}, ${postnr} ${poststed}`
	}

	return (
		<Kategori title="Boadresse">
			<div style={{ width: '100%' }}>
				<GyldigAdresseVelger settBoadresse={settBoadresse} />
				<DollyTextInput
					name="boadresse"
					size="grow"
					value={renderAdresse()}
					label="Boadresse"
					readOnly
				/>
			</div>
		</Kategori>
	)
}
