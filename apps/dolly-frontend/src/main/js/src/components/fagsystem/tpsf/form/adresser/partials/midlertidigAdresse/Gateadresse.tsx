import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '~/config/kodeverk'
import { GateadresseDetaljert } from './GateadresseDetaljert'

interface Gateadresse {
	formikBag: FormikProps<{}>
}

enum GateadresseTyper {
	TILFELDIG = 'TILFELDIG',
	POSTNR = 'POSTNR',
	KOMMUNENR = 'KOMMUNENR',
	GATE = 'GATE',
}

export const Gateadresse = ({ formikBag }: Gateadresse) => {
	const gateadresseNrInfo = 'tpsf.midlertidigAdresse.gateadresseNrInfo'
	const norskAdresse = 'tpsf.midlertidigAdresse.norskAdresse'
	const tilleggsadresse = 'tpsf.midlertidigAdresse.norskAdresse.tilleggsadresse'

	const getState = () => {
		if (_get(formikBag.values, gateadresseNrInfo)) {
			return _get(formikBag.values, `${gateadresseNrInfo}.nummertype`)
		} else if (_get(formikBag.values, `${norskAdresse}.gatenavn`)) {
			return GateadresseTyper.GATE
		} else return GateadresseTyper.TILFELDIG
	}

	const [gateAdresseType, setGateAdresseType] = useState(getState())

	const handleRadioChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
		const type = event.target.value
		setGateAdresseType(type)

		switch (type) {
			case GateadresseTyper.TILFELDIG:
				formikBag.setFieldValue(norskAdresse, {
					tilleggsadresse: _get(formikBag.values, tilleggsadresse),
				})
				formikBag.setFieldValue(gateadresseNrInfo, undefined)
				break
			case GateadresseTyper.POSTNR:
				formikBag.setFieldValue(norskAdresse, {
					tilleggsadresse: _get(formikBag.values, tilleggsadresse),
				})
				formikBag.setFieldValue(gateadresseNrInfo, {
					nummertype: GateadresseTyper.POSTNR,
					nummer: '',
				})
				break
			case GateadresseTyper.KOMMUNENR:
				formikBag.setFieldValue(norskAdresse, {
					tilleggsadresse: _get(formikBag.values, tilleggsadresse),
				})
				formikBag.setFieldValue(gateadresseNrInfo, {
					nummertype: GateadresseTyper.KOMMUNENR,
					nummer: '',
				})
				break
			case GateadresseTyper.GATE:
				formikBag.setFieldValue(norskAdresse, {
					postnr: '',
					gatenavn: '',
					gatekode: '',
					husnr: '',
					tilleggsadresse: _get(formikBag.values, tilleggsadresse),
				})
				formikBag.setFieldValue(gateadresseNrInfo, undefined)
				break
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
					{
						label: 'Tilfeldig gateadresse',
						value: GateadresseTyper.TILFELDIG,
						id: GateadresseTyper.TILFELDIG,
					},
					{
						label: 'Tilfeldig gateadresse basert på postnummer ...',
						value: GateadresseTyper.POSTNR,
						id: GateadresseTyper.POSTNR,
					},
					{
						label: 'Tilfeldig gateadresse basert på kommunenummer ...',
						value: GateadresseTyper.KOMMUNENR,
						id: GateadresseTyper.KOMMUNENR,
					},
					{
						label: 'Gateadresse detaljert ...',
						value: GateadresseTyper.GATE,
						id: GateadresseTyper.GATE,
					},
				]}
				checked={gateAdresseType}
				onChange={handleRadioChange}
			/>
			{[GateadresseTyper.POSTNR, GateadresseTyper.KOMMUNENR].includes(gateAdresseType) && (
				<Kategori title="Generer midlertidig adresse ...">
					{gateAdresseType === GateadresseTyper.POSTNR && (
						<FormikSelect
							name={`${gateadresseNrInfo}.nummer`}
							label="Postnummer"
							kodeverk={AdresseKodeverk.PostnummerUtenPostboks}
							size="large"
							isClearable={false}
						/>
					)}
					{gateAdresseType === GateadresseTyper.KOMMUNENR && (
						<FormikSelect
							name={`${gateadresseNrInfo}.nummer`}
							label="Kommunenummer"
							kodeverk={AdresseKodeverk.Kommunenummer}
							size="large"
							isClearable={false}
						/>
					)}
				</Kategori>
			)}
			{gateAdresseType === GateadresseTyper.GATE && <GateadresseDetaljert formikBag={formikBag} />}
		</div>
	)
}
