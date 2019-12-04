import React from 'react'
import * as Yup from 'yup'
import Bestillingskriterier from '~/components/bestilling/sammendrag/kriterier/Kriterier'
import { MiljoeVelgerForm } from './MiljoevelgerForm'
import { MalForm } from './MalForm'

export const Steg3 = ({ formikBag }) => {
	return (
		<div>
			{/* Egen komponent med identtype, antall osv..? */}
			{/* <div className="oppsummering">
				<Bestillingskriterier bestilling={formikBag.values} />
			</div> */}
			<MiljoeVelgerForm formikBag={formikBag} />
			<MalForm formikBag={formikBag} />
		</div>
	)
}

Steg3.label = 'Oppsummering'

Steg3.validation = Yup.object({
	// ...MiljoeVelgerForm.validation
})
