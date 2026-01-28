import React from 'react'
import { DeltBostedData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { formatDate } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import {
	BestillingData,
	BestillingTitle,
	EmptyObject,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'

type DeltBostedTypes = {
	deltBosted: DeltBostedData
}

export const DeltBostedVisning = ({ deltBosted }: DeltBostedTypes) => {
	if (isEmpty(deltBosted)) {
		return <EmptyObject />
	}

	return (
		<>
			<TitleValue
				title="Startdato for kontrakt"
				value={formatDate(deltBosted.startdatoForKontrakt)}
			/>
			<TitleValue
				title="Sluttdato for kontrakt"
				value={formatDate(deltBosted.sluttdatoForKontrakt)}
			/>
			<TitleValue title="Master" value={deltBosted.master} />
		</>
	)
}

export const DeltBosted = ({ deltBosted }: DeltBostedTypes) => {
	if (!deltBosted) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Delt bosted</BestillingTitle>
				<div className="bestilling-blokk">
					<BestillingData>
						<DeltBostedVisning deltBosted={deltBosted} />
					</BestillingData>
				</div>
			</ErrorBoundary>
		</div>
	)
}
