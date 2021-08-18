import React from 'react'
import { AttributtVelger } from './attributtVelger/AttributtVelger'
import { OrganisasjonDetaljerPanel } from './paneler/OrganisasjonDetaljer'

export const Steg1Organisasjon = ({ stateModifier }: any) => {
	const checked = [OrganisasjonDetaljerPanel]
		.map(panel => ({
			label: panel.heading,
			values: stateModifier(panel.initialValues).checked
		}))
		.filter(v => v.values.length)

	return (
		<AttributtVelger checked={checked}>
			<OrganisasjonDetaljerPanel stateModifier={stateModifier} />
		</AttributtVelger>
	)
}
