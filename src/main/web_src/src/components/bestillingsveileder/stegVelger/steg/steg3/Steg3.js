import React from 'react'
import * as Yup from 'yup'
import { harAvhukedeAttributter } from '~/components/bestillingsveileder/utils'
import Bestillingskriterier from '~/components/bestilling/sammendrag/kriterier/Kriterier'
import { MiljoVelger } from '~/components/miljoVelger/MiljoVelger'
import { MalForm } from './MalForm'
import { BestillingInfoboks } from './BestillingInfoboks'

export const Steg3 = ({ formikBag }) => {
	return (
		<div>
			{harAvhukedeAttributter(formikBag.values) && (
				<div className="oppsummering">
					<Bestillingskriterier bestilling={formikBag.values} />
					<BestillingInfoboks bestillingsdata={formikBag.values} />
				</div>
			)}
			<MiljoVelger
				bestillingsdata={formikBag.values}
				heading="Hvilke testmiljø vil du opprette i?"
			/>
			{!formikBag.values.hasOwnProperty('organisasjon') && <MalForm formikBag={formikBag} />}
		</div>
	)
}

Steg3.label = 'Oppsummering'

Steg3.validation = Yup.object(
	Object.assign(
		{},
		{
			environments: Yup.array()
				.of(Yup.string().required('Velg et navn'))
				.min(1, 'Må minst velge et miljø')
		},
		MalForm.validation
	)
)
