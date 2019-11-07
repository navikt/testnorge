import React from 'react'
import Overskrift from '~/components/ui/overskrift/Overskrift'
import { getInitialValues } from '~/service/Attributter'
import { FormikAttributtVelger } from '~/components/attributtVelger-ny/AttributtVelger'

export const Steg1 = ({ formikBag }) => {
	return (
		<div>
			<Overskrift label="Velg egenskaper" />
			<FormikAttributtVelger formikBag={formikBag} />
		</div>
	)
}

const initialValues = Object.assign(
	{},
	{
		attributter: getInitialValues()
	}
)

Steg1.label = 'Velg egenskaper'
Steg1.initialValues = initialValues
