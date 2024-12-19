import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'

type SpraakTypes = {
	spraakKode: string
}
export const Spraak = ({ spraakKode }: SpraakTypes) => {
	if (!spraakKode) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Språk</BestillingTitle>
				<BestillingData>
					<TitleValue
						title="Språk"
						value={spraakKode}
						kodeverk={PersoninformasjonKodeverk.Spraak}
					/>
				</BestillingData>
			</ErrorBoundary>
		</div>
	)
}
