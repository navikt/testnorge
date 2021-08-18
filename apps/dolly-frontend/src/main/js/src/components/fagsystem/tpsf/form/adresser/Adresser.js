import React, { useState } from 'react'
import { RadioPanelGruppe } from 'nav-frontend-skjema'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { Boadresse } from './partials/boadresse/Boadresse'
import { Postadresser } from './Postadresser'
import { MidlertidigAdresse } from './MidlertidigAdresse'
import { MatrikkelAdresse } from './partials/MatrikkelAdresse'
import { AdresseNr } from './partials/AdresseNr'
import { Tilleggsadresse } from '~/components/fagsystem/tpsf/form/adresser/partials/Tilleggsadresse/Tilleggsadresse'

const paths = ['tpsf.boadresse', 'tpsf.postadresse', 'tpsf.midlertidigAdresse']
/* Fordi UFB også bruker boadresse kan vi ikke bare sjekke den. 
Flyttedato er for nye bestillinger. Postnr (detaljert gateadr og matr) og adresseNrInfo er for maler
*/
export const boadressePaths = [
	'tpsf.boadresse.bolignr',
	'tpsf.boadresse.flyttedato',
	'tpsf.boadresse.postnr',
	'tpsf.adresseNrInfo'
]

const initialBoType = formikBag => {
	const adresseType = _get(formikBag.values, 'tpsf.boadresse.adressetype')
	const nummertype = _get(formikBag.values, 'tpsf.adresseNrInfo.nummertype')

	if (nummertype) return nummertype === 'POSTNR' ? 'postnr' : 'kommunenr'
	else if (adresseType) return adresseType === 'GATE' ? 'gate' : 'matrikkel'
	else return
}

export const Adresser = ({ formikBag }) => {
	const [boType, setBoType] = useState(initialBoType(formikBag))

	// Sjekker om adresse er valgt på steg 1, da panelet ikke skal vises dersom bare diskresjonskoder (med UFB og kommunenummer) er valgt
	const erValgt = [...boadressePaths, 'tpsf.postadresse', 'tpsf.midlertidigAdresse'].some(path =>
		_has(formikBag.values, path)
	)

	const handleRadioChange = e => {
		const nyType = e.target.value
		setBoType(nyType)

		formikBag.setFieldValue('tpsf.adresseNrInfo', null)
		formikBag.setFieldValue('tpsf.boadresse', {
			bolignr: _get(formikBag.values.tpsf.boadresse, 'bolignr') || '',
			flyttedato: _get(formikBag.values.tpsf.boadresse, 'flyttedato') || '',
			tilleggsadresse: _get(formikBag.values.tpsf.boadresse, 'tilleggsadresse') || undefined,
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
					bolignr: formikBag.values.tpsf.boadresse.bolignr,
					flyttedato: formikBag.values.tpsf.boadresse.flyttedato,
					tilleggsadresse: formikBag.values.tpsf.boadresse.tilleggsadresse
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
					bolignr: formikBag.values.tpsf.boadresse.bolignr,
					flyttedato: formikBag.values.tpsf.boadresse.flyttedato,
					tilleggsadresse: formikBag.values.tpsf.boadresse.tilleggsadresse
				})
				break
			default:
				break
		}
	}

	return (
		erValgt && (
			<Vis attributt={paths}>
				<Panel
					heading="Adresser"
					hasErrors={panelError(formikBag, paths.concat('tpsf.adresseNrInfo'))}
					iconType="adresse"
					startOpen={() => erForste(formikBag.values, paths)}
				>
					<Vis attributt={boadressePaths}>
						<RadioPanelGruppe
							name="botype"
							legend="Hva slags boadresse vil du opprette?"
							radios={[
								{
									label: 'Tilfeldig gateadresse basert på postnummer ...',
									value: 'postnr',
									id: 'postnr'
								},
								{
									label: 'Tilfeldig gateadresse basert på kommunenummer ...',
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
						<div className="flexbox--flex-wrap">
							<FormikTextInput name="tpsf.boadresse.bolignr" label="Bruksenhetsnummer" />
							<FormikDatepicker name="tpsf.boadresse.flyttedato" label="Flyttedato" />
						</div>
						<Tilleggsadresse
							formikBag={formikBag}
							tilleggsadressePath="tpsf.boadresse.tilleggsadresse"
							options="tilleggstype"
						/>
					</Vis>

					<Vis attributt="tpsf.postadresse">
						<Postadresser formikBag={formikBag} />
					</Vis>
					<Vis attributt="tpsf.midlertidigAdresse">
						<MidlertidigAdresse formikBag={formikBag} />
					</Vis>
				</Panel>
			</Vis>
		)
	)
}
