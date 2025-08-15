import React, { memo, useContext } from 'react'
import type { BestillingsveilederContextType } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import type { useStateModifierFns } from '@/components/bestillingsveileder/stateModifier'
import { Steg1Person } from './Steg1Person'
import { Steg1Organisasjon } from './Steg1Organisasjon'

export interface Steg1Props {
	stateModifier: ReturnType<typeof useStateModifierFns>
}

const Steg1: React.FC<Steg1Props> = ({ stateModifier }) => {
	const { is } = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const isOrg = is.nyOrganisasjon || is.nyStandardOrganisasjon || is.nyOrganisasjonFraMal
	return isOrg ? (
		<Steg1Organisasjon stateModifier={stateModifier} />
	) : (
		<Steg1Person stateModifier={stateModifier} />
	)
}

Steg1.displayName = 'Steg1'
export default memo(Steg1)
