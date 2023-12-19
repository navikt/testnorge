import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle, EmptyObject } from '@/components/bestilling/sammendrag/Bestillingsdata'
import React from 'react'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { OpprettNyPerson } from '@/components/fagsystem/pdlf/PdlTypes'

type PersondetaljerTypes = {
	opprettNyPerson: OpprettNyPerson
}
export const Persondetaljer = ({ opprettNyPerson }: PersondetaljerTypes) => {
	if (!opprettNyPerson) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Persondetaljer</BestillingTitle>
				<>
					{isEmpty(opprettNyPerson, ['identtype', 'syntetisk']) ? (
						<EmptyObject />
					) : (
						<div className="flexbox--flex-wrap">
							<TitleValue title="Alder" value={opprettNyPerson.alder} />
							<TitleValue title="Fødselsdato" value={formatDate(opprettNyPerson.foedtEtter)} />
							<TitleValue title="Fødselsdato" value={formatDate(opprettNyPerson.foedtFoer)} />
						</div>
					)}
				</>
			</ErrorBoundary>
		</div>
	)
}
