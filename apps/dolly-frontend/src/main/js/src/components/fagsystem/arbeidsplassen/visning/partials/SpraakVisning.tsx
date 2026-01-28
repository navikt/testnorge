import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { showLabel } from '@/utils/DataFormatter'
import { Spraak } from '@/components/fagsystem/arbeidsplassen/ArbeidsplassenTypes'

type SpraakVisningProps = {
	data?: Array<Spraak>
}

export const SpraakVisning = ({ data }: SpraakVisningProps) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Språk" nested>
					{(spraak: Spraak) => (
						<>
							<TitleValue title="Språk" value={showLabel('spraak', spraak.language)} />
							<TitleValue
								title="Muntlig"
								value={showLabel('spraakNivaa', spraak.oralProficiency)}
							/>
							<TitleValue
								title="Skriftlig"
								value={showLabel('spraakNivaa', spraak.writtenProficiency)}
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
