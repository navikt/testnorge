import React from 'react'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import { AttributtVelger } from '~/components/attributtVelger-ny/AttributtVelger'

export const Steg1 = ({ formikBag, attributter, checkAttributter }) => {
	return (
		<div>
			<Overskrift label="Velg egenskaper" />
			<AttributtVelger valgteAttributter={attributter} checkAttributter={checkAttributter} />
		</div>
	)
}

Steg1.label = 'Velg egenskaper'
Steg1.initialValues = attrs => {
	return {}
}
