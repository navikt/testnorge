import React, { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/Bestillingsveileder'
import { Steg1Person } from './Steg1Person'
import { Steg1Organisasjon } from './Steg1Organisasjon'
import { useFormikContext } from 'formik'

export const Steg1 = ({ stateModifier }) => {
	const opts = useContext(BestillingsveilederContext)
	const formikBag = useFormikContext()

	return opts.is.nyOrganisasjon ||
		opts.is.nyStandardOrganisasjon ||
		opts.is.nyOrganisasjonFraMal ? (
		<Steg1Organisasjon formikBag={formikBag} stateModifier={stateModifier} />
	) : (
		<Steg1Person formikBag={formikBag} stateModifier={stateModifier} />
	)
}

Steg1.label = 'Velg egenskaper'
