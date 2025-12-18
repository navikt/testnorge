import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'
import { NavnBestilling } from '@/components/fagsystem/pdlf/PdlTypes'

type NavnTypes = {
	navnListe: Array<NavnBestilling>
}

export const Navn = ({ navnListe }: NavnTypes) => {
	if (!navnListe || navnListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Navn</BestillingTitle>
				<DollyFieldArray header="Navn" data={navnListe}>
					{(navn: NavnBestilling, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="Fornavn" value={navn.fornavn} />
								<TitleValue title="Mellomnavn" value={navn.mellomnavn} />
								<TitleValue title="Etternavn" value={navn.etternavn} />
								<TitleValue
									title="Har tilfeldig mellomnavn"
									value={oversettBoolean(navn.hasMellomnavn)}
								/>
								<TitleValue title="Gyldig f.o.m. dato" value={formatDate(navn.gyldigFraOgMed)} />
								<TitleValue title="Master" value={navn.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
