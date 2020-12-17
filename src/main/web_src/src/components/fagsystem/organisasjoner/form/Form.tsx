import React from 'react'
import { Detaljer } from './Detaljer'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'

const organisasjonPaths = [
	'organisasjon.enhetstype',
	'organisasjon.naeringskode',
	'organisasjon.formaal'
]

const kontaktPaths = [
	'organisasjon.telefon',
	// 'organisasjon.mobiltelefon',
	'organisasjon.epost',
	'organisasjon.nettadresse'
]

const adressePaths = ['organisasjon.forretningsadresse', 'organisasjon.postadresse']

const detaljerPaths = [organisasjonPaths, kontaktPaths, adressePaths].flat()

export const OrganisasjonForm = ({ formikBag }) => {
	return (
		<>
			<Vis attributt={detaljerPaths}>
				<Panel
					heading="Detaljer"
					hasErrors={panelError(formikBag, detaljerPaths)}
					iconType={'personinformasjon'}
					startOpen={() => erForste(formikBag.values, detaljerPaths)}
				>
					<Detaljer formikBag={formikBag} path="organisasjon" level={0} />
				</Panel>
			</Vis>
			{/* Flere kategorier legges inn her */}
		</>
	)
}

// TODO: validation!
