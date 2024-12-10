import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { oversettBoolean } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { GenerertPensjonsgivendeInntektTypes } from '@/components/fagsystem/pensjon/PensjonTypes'

type GenerertPensjonsgivendeInntektProps = {
	pensjon: GenerertPensjonsgivendeInntektTypes
}

export const GenerertPensjonsgivendeInntekt = ({
	pensjon,
}: GenerertPensjonsgivendeInntektProps) => {
	if (!pensjon || isEmpty(pensjon)) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Generert pensjonsgivende inntekt (POPP)</BestillingTitle>
				<BestillingData>
					<TitleValue title="Fra og med år" value={pensjon?.fomAar} />
					<TitleValue title="Til og med år" value={pensjon?.tomAar} />
					<TitleValue title="Gjennomsnitt G-verdi" value={pensjon?.averageG} />
					<TitleValue
						title="Tillat inntekt under 1G"
						value={oversettBoolean(pensjon?.tillatInntektUnder1G)}
					/>
				</BestillingData>
			</ErrorBoundary>
		</div>
	)
}
