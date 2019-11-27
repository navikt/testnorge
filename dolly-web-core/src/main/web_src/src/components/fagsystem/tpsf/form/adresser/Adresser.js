import React, { useState } from 'react'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { Boadresse } from './partials/boadresse/Boadresse'
import { MatrikkelAdresse } from './partials/MatrikkelAdresse'
import { AdresseNr } from './partials/AdresseNr'

const paths = [pathAttrs.kategori.boadresse, pathAttrs.kategori.postadresse].flat()

export const Adresser = ({ formikBag }) => {
	const [boType, setBoType] = useState()

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
			case 'matrikkel':
				formikBag.setFieldValue('tpsf.boadresse', {
					adressetype: 'GATE',
					mellomnavn: '',
					gardsnr: '',
					bruksnr: '',
					festnr: '',
					undernr: '',
					postnr: ''
				})

			default:
				break
		}
	}

	return (
		<Vis attributt={paths}>
			<Panel heading="Adresser" startOpen>
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
