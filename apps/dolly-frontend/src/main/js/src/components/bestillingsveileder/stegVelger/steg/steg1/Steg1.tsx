import React, { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { Steg1Person } from './Steg1Person'
import { Steg1Organisasjon } from './Steg1Organisasjon'

const Steg1 = ({ stateModifier }) => {
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	return opts.is.nyOrganisasjon ||
		opts.is.nyStandardOrganisasjon ||
		opts.is.nyOrganisasjonFraMal ? (
		<Steg1Organisasjon stateModifier={stateModifier} />
	) : (
		<Steg1Person stateModifier={stateModifier} />
	)
}

export default Steg1
