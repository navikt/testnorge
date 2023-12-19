import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { DoedsfallData } from '@/components/fagsystem/pdlf/PdlTypes'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'

type DoedsfallTypes = {
	doedsfallListe: Array<DoedsfallData>
}

export const Doedsfall = ({ doedsfallListe }: DoedsfallTypes) => {
	if (!doedsfallListe || doedsfallListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Dødsfall</BestillingTitle>
				<DollyFieldArray header="Dødsfall" data={doedsfallListe}>
					{(doedsfall: DoedsfallData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="Dødsdato" value={formatDate(doedsfall.doedsdato)} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
