import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'

type NomdataTypes = {
	nomdata: {
		startDato: string
		sluttDato?: string
	}
}

export const NavAnsatt = ({ nomdata }: NomdataTypes) => {
	if (!nomdata) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Nav-ansatt (NOM)</BestillingTitle>
				<BestillingData>
					{!nomdata.startDato && !nomdata.sluttDato ? (
						<TitleValue title="Nav-ansatt" value="Ingen verdier satt" />
					) : (
						<>
							<TitleValue title="Startdato" value={formatDate(nomdata.startDato)} />
							<TitleValue title="Sluttdato" value={formatDate(nomdata.sluttDato)} />
						</>
					)}
				</BestillingData>
			</ErrorBoundary>
		</div>
	)
}
