import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
	EmptyObject,
} from '@/components/bestilling/sammendrag/Bestillingsdata'
import React from 'react'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { OpprettNyPerson } from '@/components/fagsystem/pdlf/PdlTypes'
import _ from 'lodash'

type AlderTypes = {
	opprettNyPerson: OpprettNyPerson
}
export const Alder = ({ opprettNyPerson }: AlderTypes) => {
	if (!opprettNyPerson || !_.has(opprettNyPerson, 'alder')) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Alder</BestillingTitle>
				<>
					{isEmpty(opprettNyPerson, ['identtype', 'syntetisk']) ? (
						<EmptyObject />
					) : (
						<BestillingData>
							<TitleValue title="Alder" value={opprettNyPerson.alder} />
							<TitleValue title="Født etter" value={formatDate(opprettNyPerson.foedtEtter)} />
							<TitleValue title="Født før" value={formatDate(opprettNyPerson.foedtFoer)} />
						</BestillingData>
					)}
				</>
			</ErrorBoundary>
		</div>
	)
}
