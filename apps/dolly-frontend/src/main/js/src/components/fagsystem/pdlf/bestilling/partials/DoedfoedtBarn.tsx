import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { DoedfoedtBarnData } from '@/components/fagsystem/pdlf/PdlTypes'

type DoedfoedtBarnTypes = {
	doedfoedtBarnListe: Array<DoedfoedtBarnData>
}

export const DoedfoedtBarn = ({ doedfoedtBarnListe }: DoedfoedtBarnTypes) => {
	if (!doedfoedtBarnListe || doedfoedtBarnListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Dødfødt barn</BestillingTitle>
				<DollyFieldArray header="Dødfødt barn" data={doedfoedtBarnListe}>
					{(doedfoedtBarn: DoedfoedtBarnData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="Dødsdato" value={formatDate(doedfoedtBarn.dato)} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
