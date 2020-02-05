import React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { GyldigAdresseVelger } from './GyldigAdresseVelger/GyldigAdresseVelger'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'

import './Boadresse.less'

export const Boadresse = ({ formikBag }) => {
	const settBoadresse = adresse => {
		formikBag.setFieldValue('tpsf.boadresse', {
			flyttedato: formikBag.values.tpsf.boadresse.flyttedato
		})
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

	const feilmelding = () => {
		if (
			!_get(formikBag.values, 'tpsf.boadresse.gateadresse') &&
			_has(formikBag.touched, 'tpsf.boadresse.gateadresse')
		) {
			return { feilmelding: _get(formikBag.errors, 'tpsf.boadresse.gateadresse') }
		}
	}

	return (
		<Kategori title="Gateadresse">
			<div className="gateadresse">
				<GyldigAdresseVelger settBoadresse={settBoadresse} />
				<DollyTextInput
					name="boadresse"
					size="grow"
					value={renderAdresse()}
					label="Boadresse"
					readOnly
					placeholder="Ingen valgt adresse"
					title="Endre adressen i adressevelgeren over"
					feil={feilmelding()}
				/>
			</div>
		</Kategori>
	)
}
