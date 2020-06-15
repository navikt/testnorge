import React, { useState } from 'react'
import { FormikProps } from 'formik'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { GateadresseDetaljert } from './GateadresseDetaljert'

interface Gateadresse {
	formikBag: FormikProps<{}>
}

//TODO: Enum for type?

export const Gateadresse = ({ formikBag }: Gateadresse) => {
	const [gateAdresseType, setGateAdresseType] = useState('TILFELDIG')
	// TODO: Hent state fra formikBag

	const handleRadioChange = (v: any) => {
		const type = v.target.value
		setGateAdresseType(type)

		switch (type) {
			case 'TILFELDIG':
				formikBag.setFieldValue('tpsf.midlertidigAdresse.norskAdresse', undefined)
				formikBag.setFieldValue('tpsf.midlertidigAdresse.gateadresseNrInfo', undefined)
				// Eller objekt med tomme stringer????
				break
			case 'POSTNR':
				formikBag.setFieldValue('tpsf.midlertidigAdresse.norskAdresse', undefined)
				formikBag.setFieldValue('tpsf.midlertidigAdresse.gateadresseNrInfo', {
					nummertype: 'POSTNR',
					nummer: ''
				})
				break
			case 'KOMMUNENR':
				formikBag.setFieldValue('tpsf.midlertidigAdresse.norskAdresse', undefined)
				formikBag.setFieldValue('tpsf.midlertidigAdresse.gateadresseNrInfo', {
					nummertype: 'KOMMUNENR',
					nummer: ''
				})
				break
			case 'GATE':
				formikBag.setFieldValue('tpsf.midlertidigAdresse.norskAdresse', {
					postnr: '',
					gatenavn: '',
					gatekode: '',
					husnr: ''
				})
				formikBag.setFieldValue('tpsf.midlertidigAdresse.gateadresseNrInfo', undefined)
			default:
				break
		}
	}

	return (
		<div className="flexbox--full-width">
			{/* 
            // @ts-ignore */}
			<RadioPanelGruppe
				name="gateAdresseType"
				legend="Hva slags midlertidig gateadresse vil du opprette?"
				radios={[
					{ label: 'Tilfeldig gateadresse', value: 'TILFELDIG', id: 'TILFELDIG' },
					{
						label: 'Tilfeldig gateadresse basert på postnummer ...',
						value: 'POSTNR',
						id: 'POSTNR'
					},
					{
						label: 'Tilfeldig gateadresse basert på kommunenummer ...',
						value: 'KOMMUNENR',
						id: 'KOMMUNENR'
					},
					{ label: 'Gateadresse detaljert ...', value: 'GATE', id: 'GATE' }
				]}
				checked={gateAdresseType}
				onChange={handleRadioChange}
			/>
			{['POSTNR', 'KOMMUNENR'].includes(gateAdresseType) && (
				<Kategori title="Generer midlertidig adresse ...">
					{gateAdresseType === 'POSTNR' && (
						<FormikSelect
							name="tpsf.midlertidigAdresse.gateadresseNrInfo.nummer"
							label="Postnummer"
							kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
							size="large"
							isClearable={false}
						/>
					)}
					{gateAdresseType === 'KOMMUNENR' && (
						<FormikSelect
							name="tpsf.midlertidigAdresse.gateadresseNrInfo.nummer"
							label="Kommunenummer"
							kodeverk={AdresseKodeverk.Kommunenummer}
							size="large"
							isClearable={false}
						/>
					)}
				</Kategori>
			)}
			{gateAdresseType === 'GATE' && <GateadresseDetaljert formikBag={formikBag} />}
		</div>
	)
}
