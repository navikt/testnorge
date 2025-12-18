import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import React from 'react'
import { Bestillingsinformasjon } from '@/components/bestilling/sammendrag/visning/Bestillingsinformasjon'
import { Bestillingsdata } from '@/components/bestilling/sammendrag/Bestillingsdata'

export const BestillingKriterier = ({ bestilling }) => {
	return (
		<div className="bestilling-data">
			<SubOverskrift label="Bestillingskriterier" />
			<Bestillingsinformasjon bestillingsinfo={bestilling} />
			<Bestillingsdata bestilling={bestilling.bestilling} erGruppevisning />
		</div>
	)
}
