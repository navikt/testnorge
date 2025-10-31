import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import React from 'react'
import { AlderspensjonTypes } from '@/components/fagsystem/alderspensjon/AlderspensjonTypes'
import { useNavEnheter } from '@/utils/hooks/useNorg2'

type AlderspensjonProps = {
	pensjon?: AlderspensjonTypes
}

export const Alderspensjon = ({ pensjon }: AlderspensjonProps) => {
	const { navEnheter } = useNavEnheter()

	if (!pensjon || isEmpty(pensjon)) {
		return null
	}

	const navEnhetLabel = navEnheter?.find(
		(enhet) => enhet.value === pensjon.navEnhetId?.toString(),
	)?.label

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>
					{'Alderspensjon: ' + (pensjon?.soknad ? 'Søknad' : 'Vedtak')}
				</BestillingTitle>
				<BestillingData>
					<TitleValue title="Krav fremsatt dato" value={formatDate(pensjon?.kravFremsattDato)} />
					<TitleValue title="Iverksettelsesdato" value={formatDate(pensjon?.iverksettelsesdato)} />
					<TitleValue title="Saksbehandler" value={pensjon?.saksbehandler} />
					<TitleValue title="Attesterer" value={pensjon?.attesterer} />
					<TitleValue title="Uttaksgrad" value={`${pensjon?.uttaksgrad}%`} />
					<TitleValue title="NAV-kontor" value={navEnhetLabel || pensjon?.navEnhetId} />
					<TitleValue
						title="Ektefelle/partners inntekt"
						value={pensjon?.relasjoner?.[0]?.sumAvForvArbKapPenInntekt}
					/>
				</BestillingData>
			</ErrorBoundary>
		</div>
	)
}
