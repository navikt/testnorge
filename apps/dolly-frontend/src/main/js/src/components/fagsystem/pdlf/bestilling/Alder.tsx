import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
	EmptyObject,
} from '@/components/bestilling/sammendrag/Bestillingsvisning'
import React from 'react'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { OpprettNyPerson } from '@/components/fagsystem/pdlf/PdlTypes'
import _ from 'lodash'

type AlderTypes = {
	opprettNyPerson: OpprettNyPerson
	erGruppevisning?: boolean
}
export const Alder = ({ opprettNyPerson, erGruppevisning = false }: AlderTypes) => {
	const alderIsEmpty = isEmpty(opprettNyPerson, ['identtype', 'syntetisk', 'id2032'])
	const alderIsUndefined =
		!_.has(opprettNyPerson, 'alder') &&
		!_.has(opprettNyPerson, 'foedtEtter') &&
		!_.has(opprettNyPerson, 'foedtFoer')

	if (!opprettNyPerson || alderIsUndefined || (erGruppevisning && alderIsEmpty)) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Alder</BestillingTitle>
				<>
					{alderIsEmpty ? (
						<EmptyObject />
					) : (
						<div className="bestilling-blokk">
							<BestillingData>
								<TitleValue title="Alder" value={opprettNyPerson.alder} />
								<TitleValue title="Født etter" value={formatDate(opprettNyPerson.foedtEtter)} />
								<TitleValue title="Født før" value={formatDate(opprettNyPerson.foedtFoer)} />
							</BestillingData>
						</div>
					)}
				</>
			</ErrorBoundary>
		</div>
	)
}
