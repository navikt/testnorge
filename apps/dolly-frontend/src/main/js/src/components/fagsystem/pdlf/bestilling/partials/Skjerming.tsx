import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'

type SkjermingTypes = {
	skjerming: {
		egenAnsattDatoFom: string
		egenAnsattDatoTom?: string
	}
}
export const Skjerming = ({ skjerming }: SkjermingTypes) => {
	if (!skjerming) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Skjerming</BestillingTitle>
				<div className="flexbox--flex-wrap" style={{ marginBottom: '10px' }}>
					<TitleValue title="Skjerming fra" value={formatDate(skjerming.egenAnsattDatoFom)} />
					<TitleValue title="Skjerming til" value={formatDate(skjerming.egenAnsattDatoTom)} />
				</div>
			</ErrorBoundary>
		</div>
	)
}
