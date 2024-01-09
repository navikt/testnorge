import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'

type NorskBankkontoTypes = {
	norskBankkonto: {
		kontonummer: string
		tilfeldigKontonummer: boolean
	}
}
export const NorskBankkonto = ({ norskBankkonto }: NorskBankkontoTypes) => {
	if (!norskBankkonto) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Norsk bankkonto</BestillingTitle>
				<BestillingData>
					<TitleValue title="Kontonummer" value={norskBankkonto.kontonummer} />
					<TitleValue
						title="Tilfeldig kontonummer"
						value={norskBankkonto.tilfeldigKontonummer && 'Ja'}
					/>
				</BestillingData>
			</ErrorBoundary>
		</div>
	)
}
