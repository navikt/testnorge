import React, { useState } from 'react'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import _get from 'lodash/get'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { Boadresse } from './partials/boadresse/Boadresse'
import { MatrikkelAdresse } from './partials/MatrikkelAdresse'
import { AdresseNr } from './partials/AdresseNr'

const paths = [pathAttrs.kategori.boadresse, pathAttrs.kategori.postadresse].flat()

const initialBoType = formikBag => {
	const adresseType = _get(formikBag.values.tpsf.boadresse, 'adressetype')
	const nummertype = _get(formikBag.values.tpsf.adresseNrInfo, 'nummertype')

	if (adresseType) return adresseType === 'GATE' ? 'gate' : 'matrikkel'
	else if (nummertype) return nummertype === 'POSTNR' ? 'postnr' : 'kommunenr'
	else return
}

export const Adresser = ({ formikBag }) => {
	const [boType, setBoType] = useState(initialBoType(formikBag))

	// Unngå at Adresse-panel vises når kommunenr blir satt på diskresjonskoder
	const harBoadresse = Boolean('boadresse' in formikBag.initialValues.tpsf)
	if (!harBoadresse) return null

	const handleRadioChange = e => {
		const nyType = e.target.value
		setBoType(nyType)

		formikBag.setFieldValue('tpsf.adresseNrInfo', null)
		formikBag.setFieldValue('tpsf.boadresse', null)

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
					husnummer: ''
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
					postnr: ''
				})
				break
			default:
				break
		}
	}

	return (
		<Vis attributt={paths}>
			<Panel heading="Adresser">
				<Vis attributt="tpsf.boadresse">
					<RadioPanelGruppe
						name="botype"
						legend="Hva slags adresse vil du opprette?"
						radios={[
							{ label: 'Postnummer ...', value: 'postnr', id: 'postnr' },
							{ label: 'Kommunenummer ...', value: 'kommunenr', id: 'kommunenr' },
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
				</Vis>

				{/* <Vis attributt="tpsf.postadresse">
					<span>postadresse komponent</span>
                    <FormikDatepicker name="tpsf.boadresse.flyttedato" label="Flyttedato" />
				</Vis> */}
			</Panel>
		</Vis>
	)
}
