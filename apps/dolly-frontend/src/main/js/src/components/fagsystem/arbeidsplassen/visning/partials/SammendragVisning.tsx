import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'

type SammendragVisningProps = {
	data?: string
}

export const SammendragVisning = ({ data }: SammendragVisningProps) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<div className="flexbox--full-width">
					<h3>Sammendrag</h3>
					<TitleValue title="Oppsummering" value={data} size="fullWidth" />
				</div>
			</ErrorBoundary>
		</div>
	)
}
