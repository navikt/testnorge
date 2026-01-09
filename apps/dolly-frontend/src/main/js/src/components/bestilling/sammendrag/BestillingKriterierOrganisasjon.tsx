import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Bestillingsinformasjon } from '@/components/bestilling/sammendrag/visning/Bestillingsinformasjon'
import React from 'react'
import { BestillingsdataOrganisasjon } from '@/components/bestilling/sammendrag/BestillingsdataOrganisasjon'

export const BestillingKriterierOrganisasjon = ({ bestilling }) => {
	return (
		<div className="bestilling-data">
			<SubOverskrift label="Bestillingskriterier" />
			<Bestillingsinformasjon bestillingsinfo={bestilling} />
			<BestillingsdataOrganisasjon bestilling={bestilling.bestilling} />
		</div>
	)
}
