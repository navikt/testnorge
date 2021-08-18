import React, { useContext } from 'react'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { Steg1Person } from './Steg1Person'
import { Steg1Organisasjon } from './Steg1Organisasjon'

export const Steg1 = ({ formikBag, stateModifier }) => {
	const opts = useContext(BestillingsveilederContext)

	return opts.is.nyOrganisasjon || opts.is.nyStandardOrganisasjon ? (
		<Steg1Organisasjon formikBag={formikBag} stateModifier={stateModifier} />
	) : (
		<Steg1Person formikBag={formikBag} stateModifier={stateModifier} />
	)
}

Steg1.label = 'Velg egenskaper'
