import MiljoeStatus from './miljoeStatus/MiljoeStatus'
import React, { Suspense } from 'react'
import Loading from '@/components/ui/loading/Loading'
import { BestillingKriterier } from '@/components/bestilling/sammendrag/BestillingKriterier'
import * as _ from 'lodash-es'
import { BestillingKriterierOrganisasjon } from '@/components/bestilling/sammendrag/BestillingKriterierOrganisasjon'

const Bestillingskriterier = React.lazy(
	() => import('@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'),
)

export default function BestillingSammendrag({
	bestilling,
	closeModal,
}: {
	bestilling: any
	closeModal?: () => void
}) {
	const erOrganisasjon = _.has(bestilling, 'organisasjonNummer')

	return (
		<>
			<MiljoeStatus bestilling={bestilling} closeModal={closeModal} />
			<Suspense fallback={<Loading label={'Laster bestillingskriterier ...'} />}>
				{erOrganisasjon ? (
					<BestillingKriterierOrganisasjon bestilling={bestilling} />
				) : (
					<BestillingKriterier bestilling={bestilling} />
				)}
			</Suspense>
		</>
	)
}
