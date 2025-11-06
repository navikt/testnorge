import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import React from 'react'
import { AlderspensjonTypes } from '@/components/fagsystem/alderspensjon/AlderspensjonTypes'
import { useNavEnheter } from '@/utils/hooks/useNorg2'

type AlderspensjonNyUttaksgradProps = {
	apNy: AlderspensjonTypes
}

export const AlderspensjonNyUttaksgrad = ({ apNy }: AlderspensjonNyUttaksgradProps) => {
	const { navEnheter } = useNavEnheter()

	if (!apNy || isEmpty(apNy)) {
		return null
	}

	const navEnhetLabel = navEnheter?.find(
		(enhet) => enhet.value === apNy.navEnhetId?.toString(),
	)?.label

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Alderspensjon: Ny uttaksgrad</BestillingTitle>
				<BestillingData>
					<TitleValue title="Ny uttaksgrad" value={`${apNy.nyUttaksgrad}%`} />
					<TitleValue title="Dato f.o.m." value={formatDate(apNy.fom)} />
					<TitleValue title="Saksbehandler" value={apNy?.saksbehandler} />
					<TitleValue title="Attesterer" value={apNy?.attesterer} />
					<TitleValue title="NAV-kontor" value={navEnhetLabel || apNy?.navEnhetId} />
				</BestillingData>
			</ErrorBoundary>
		</div>
	)
}
