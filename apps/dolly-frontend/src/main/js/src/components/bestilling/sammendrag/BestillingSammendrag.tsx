import MiljoeStatus from './miljoeStatus/MiljoeStatus'
import React, { Suspense } from 'react'
import Loading from '@/components/ui/loading/Loading'
import * as _ from 'lodash-es'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Bestillingsinformasjon } from '@/components/bestilling/sammendrag/visning/Bestillingsinformasjon'
import { BestillingsdataOrganisasjon } from '@/components/bestilling/sammendrag/BestillingsdataOrganisasjon'
import { Bestillingsdata } from '@/components/bestilling/sammendrag/Bestillingsdata'

export const BestillingSammendrag = ({
	bestilling,
	closeModal,
}: {
	bestilling: any
	closeModal?: () => void
}) => {
	const erOrganisasjon = _.has(bestilling, 'organisasjonNummer')

	return (
		<>
			<MiljoeStatus bestilling={bestilling} closeModal={closeModal} />
			<Suspense fallback={<Loading label={'Laster bestillingskriterier ...'} />}>
				<div className="bestilling-data">
					<SubOverskrift label="Bestillingskriterier" />
					<Bestillingsinformasjon bestillingsinfo={bestilling} />
					{erOrganisasjon ? (
						<BestillingsdataOrganisasjon bestilling={bestilling.bestilling} />
					) : (
						<Bestillingsdata bestilling={bestilling.bestilling} erGruppevisning />
					)}
				</div>
			</Suspense>
		</>
	)
}
