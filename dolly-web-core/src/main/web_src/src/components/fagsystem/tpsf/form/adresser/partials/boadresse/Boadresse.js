import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { GyldigAdresseVelger } from './GyldigAdresseVelger/GyldigAdresseVelger'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

export const Boadresse = ({ formikBag }) => {
	const settBoadresse = adresse => {
		formikBag.setFieldValue('tpsf.boadresse', {})
		formikBag.setFieldValue('tpsf.boadresse.adressetype', 'GATE')
		formikBag.setFieldValue('tpsf.boadresse.gateadresse', adresse.gateadresse)
		formikBag.setFieldValue('tpsf.boadresse.postnr', adresse.postnr)
		formikBag.setFieldValue('tpsf.boadresse.poststed', adresse.poststed)
		formikBag.setFieldValue('tpsf.boadresse.kommunenr', adresse.kommunenr)
		formikBag.setFieldValue('tpsf.boadresse.gatekode', adresse.gatekode)
		formikBag.setFieldValue('tpsf.boadresse.husnummer', adresse.husnummer)
	}

	const renderAdresse = () => {
		const { gateadresse, husnummer, postnr, poststed } = formikBag.values.tpsf.boadresse
		if (!gateadresse) return ''
		return `${gateadresse} ${parseInt(husnummer)}, ${postnr} ${poststed}`
	}

	return (
		<Kategori title="Gateadresse">
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
