import { NyIdent } from '@/components/fagsystem/pdlf/PdlTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle, EmptyObject } from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'

type NyIdentitetTypes = {
	nyIdentitetListe: Array<NyIdent>
}

export const NyIdentitet = ({ nyIdentitetListe }: NyIdentitetTypes) => {
	if (!nyIdentitetListe || nyIdentitetListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Ny identitet</BestillingTitle>
				<DollyFieldArray header="Ny identitet" data={nyIdentitetListe}>
					{(nyIdentitet: NyIdent, idx: number) => {
						return (
							<React.Fragment key={idx}>
								{isEmpty(nyIdentitet, ['kilde', 'master', 'syntetisk']) ? (
									<EmptyObject />
								) : (
									<>
										<TitleValue title="Eksisterende ident" value={nyIdentitet.eksisterendeIdent} />
										<TitleValue title="Identtype" value={nyIdentitet.identtype} />
										<TitleValue title="Kjønn" value={nyIdentitet.kjoenn} />
										<TitleValue title="Født etter" value={formatDate(nyIdentitet.foedtEtter)} />
										<TitleValue title="Født før" value={formatDate(nyIdentitet.foedtFoer)} />
										<TitleValue title="Alder" value={nyIdentitet.alder} />
										<TitleValue
											title="Har mellomnavn"
											value={nyIdentitet.nyttNavn?.hasMellomnavn && 'JA'}
										/>
										<TitleValue title="Master" value={nyIdentitet.master} />
									</>
								)}
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
