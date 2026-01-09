import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { oversettBoolean } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { PensjonsgivendeInntektTypes } from '@/components/fagsystem/pensjon/PensjonTypes'

type PensjonsgivendeInntektProps = {
	pensjon: PensjonsgivendeInntektTypes
}

export const PensjonsgivendeInntekt = ({ pensjon }: PensjonsgivendeInntektProps) => {
	if (!pensjon || isEmpty(pensjon)) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Pensjonsgivende inntekt (POPP)</BestillingTitle>
				<div className="bestilling-blokk">
					<BestillingData>
						<TitleValue title="Fra og med år" value={pensjon?.fomAar} />
						<TitleValue title="Til og med år" value={pensjon?.tomAar} />
						<TitleValue title="Beløp" value={pensjon?.belop} />
						<TitleValue
							title="Nedjuster med grunnbeløp"
							value={oversettBoolean(pensjon?.redusertMedGrunnbelop)}
						/>
					</BestillingData>
				</div>
			</ErrorBoundary>
		</div>
	)
}
