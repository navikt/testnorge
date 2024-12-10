import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { oversettBoolean } from '@/utils/DataFormatter'

type HjemmelVisningProps = {
	hjemmel: boolean
}

export const HjemmelVisning = ({ hjemmel }: HjemmelVisningProps) => {
	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<div className="flexbox--full-width">
					<h3>Hjemmel</h3>
					<TitleValue title="Godta hjemmel" value={oversettBoolean(hjemmel)} />
				</div>
			</ErrorBoundary>
		</div>
	)
}
