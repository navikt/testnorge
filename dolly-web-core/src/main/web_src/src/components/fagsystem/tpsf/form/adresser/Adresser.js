import React, { useState } from 'react'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import Panel from '~/components/ui/panel/Panel'
import { Boadresse } from './partials/boadresse/Boadresse'
import { Postadresser } from './Postadresser'
import { MatrikkelAdresse } from './partials/MatrikkelAdresse'
import { AdresseNr } from './partials/AdresseNr'

const paths = ['tpsf.boadresse', 'tpsf.postadresse']

const initialBoType = formikBag => {
	const adresseType = _get(formikBag.values, 'tpsf.boadresse.adressetype')
	const nummertype = _get(formikBag.values, 'tpsf.adresseNrInfo.nummertype')

	if (nummertype) return nummertype === 'POSTNR' ? 'postnr' : 'kommunenr'
	else if (adresseType) return adresseType === 'GATE' ? 'gate' : 'matrikkel'
	else return
}

export const Adresser = ({ formikBag }) => {
	const [boType, setBoType] = useState(initialBoType(formikBag))

	const handleRadioChange = e => {
		const nyType = e.target.value
		setBoType(nyType)

		formikBag.setFieldValue('tpsf.adresseNrInfo', null)
		formikBag.setFieldValue('tpsf.boadresse', {
			flyttedato: _get(formikBag.values.tpsf.boadresse, 'flyttedato') || '',
			adressetype: 'GATE'
		})

		// Set and clear values
		switch (nyType) {
			case 'postnr':
				formikBag.setFieldValue('tpsf.adresseNrInfo', {
					nummertype: 'POSTNR',
					nummer: ''
				})
				break
			case 'kommunenr':
				formikBag.setFieldValue('tpsf.adresseNrInfo', {
					nummertype: 'KOMMUNENR',
					nummer: ''
				})
				break
			case 'gate':
				formikBag.setFieldValue('tpsf.boadresse', {
					adressetype: 'GATE',
					gateadresse: '',
					postnr: '',
					poststed: '',
					kommunenr: '',
					gatekode: '',
					husnummer: '',
					flyttedato: formikBag.values.tpsf.boadresse.flyttedato
				})
				break
			case 'matrikkel':
				formikBag.setFieldValue('tpsf.boadresse', {
					adressetype: 'MATR',
					mellomnavn: '',
					gardsnr: '',
					bruksnr: '',
					festnr: '',
					undernr: '',
					postnr: '',
					kommunenr: '',
					flyttedato: formikBag.values.tpsf.boadresse.flyttedato
				})
				break
			default:
				break
		}
	}

	return (
		<Vis attributt={paths}>
			<Panel heading="Adresser" iconType="adresse">
				<Vis attributt="tpsf.boadresse">
					<RadioPanelGruppe
						name="botype"
						legend="Hva slags adresse vil du opprette?"
						radios={[
							{
								label: 'Tilfeldig adresse basert på postnummer ...',
								value: 'postnr',
								id: 'postnr'
							},
							{
								label: 'Tilfeldig adresse basert på kommunenummer ...',
								value: 'kommunenr',
								id: 'kommunenr'
							},
							{ label: 'Gateadresse detaljert ...', value: 'gate', id: 'gate' },
							{ label: 'Matrikkeladresse detaljert ...', value: 'matrikkel', id: 'matrikkel' }
						]}
						checked={boType}
						onChange={handleRadioChange}
					/>

					{['postnr', 'kommunenr'].includes(boType) && (
						<AdresseNr formikBag={formikBag} type={boType} />
					)}
					{boType === 'gate' && <Boadresse formikBag={formikBag} />}
					{boType === 'matrikkel' && <MatrikkelAdresse formikBag={formikBag} />}
					<FormikDatepicker name="tpsf.boadresse.flyttedato" label="Flyttedato" />
				</Vis>

				{
					<Vis attributt="tpsf.postadresse">
						<Postadresser formikBag={formikBag} />
					</Vis>
				}
			</Panel>
		</Vis>
	)
}
