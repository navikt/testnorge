import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'

export const FagbrevVisning = ({ data }) => {
	if (data?.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Fagbrev" nested>
					{(fagbrev, idx: number) => (
						<>
							<TitleValue
								title="Fagdokumentasjon"
								value={Formatters.showLabel('fagbrev', fagbrev.title)}
								size="medium"
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
