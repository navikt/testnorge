import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import React from 'react'
import { Bestillingsvisning } from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { Bestillingsinformasjon } from '@/components/bestilling/sammendrag/visning/Bestillingsinformasjon'
import { Bestillingsdata } from '@/components/bestilling/sammendrag/Bestillingsdata'

export const BestillingKriterier = ({ bestilling }) => {
	return (
		<div className="bestilling-data">
			<SubOverskrift label="Bestillingskriterier" />
			<div className="bestilling-detaljer">
				<Bestillingsinformasjon bestillingsinfo={bestilling} />
				{/*<Bestillingsvisning bestilling={bestilling.bestilling} />*/}
				<Bestillingsdata bestilling={bestilling.bestilling} />
			</div>
		</div>
	)
}
