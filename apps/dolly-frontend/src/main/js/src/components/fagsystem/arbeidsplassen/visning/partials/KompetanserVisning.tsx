import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { showLabel } from '@/utils/DataFormatter'
import { Kompetanser } from '@/components/fagsystem/arbeidsplassen/ArbeidsplassenTypes'

type KompetanserVisningProps = {
	data?: Array<Kompetanser>
}

export const KompetanserVisning = ({ data }: KompetanserVisningProps) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Kompetanser" nested>
					{(kompetanse: Kompetanser) => (
						<>
							<TitleValue
								title="Kompetanse/ferdighet/verktøy"
								value={showLabel('kompetanse', kompetanse.title)}
								size="medium"
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
