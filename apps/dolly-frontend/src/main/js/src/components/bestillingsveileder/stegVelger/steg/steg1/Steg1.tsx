import React, { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { Steg1Person } from './Steg1Person'
import { Steg1Organisasjon } from './Steg1Organisasjon'

const Steg1 = ({ stateModifier }) => {
	const opts: any = useContext(BestillingsveilederContext)

	return opts.is.nyOrganisasjon ||
		opts.is.nyStandardOrganisasjon ||
		opts.is.nyOrganisasjonFraMal ? (
		<Steg1Organisasjon stateModifier={stateModifier} />
	) : (
		<Steg1Person stateModifier={stateModifier} />
	)
}

export default Steg1
