import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
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
				<div className="flexbox--flex-wrap" style={{ marginBottom: '10px' }}>
					<TitleValue
						title="Språk"
						value={spraakKode}
						kodeverk={PersoninformasjonKodeverk.Spraak}
					/>
				</div>
			</ErrorBoundary>
		</div>
	)
}
