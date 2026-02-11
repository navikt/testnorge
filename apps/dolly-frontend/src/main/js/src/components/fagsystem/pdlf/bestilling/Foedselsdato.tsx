import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingTitle,
	EmptyObject,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FoedselsdatoData } from '@/components/fagsystem/pdlf/PdlTypes'
import React from 'react'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'

type FoedselsdatoTypes = {
	foedselsdatoListe: Array<FoedselsdatoData>
}

export const Foedselsdato = ({ foedselsdatoListe }: FoedselsdatoTypes) => {
	if (!foedselsdatoListe || foedselsdatoListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Fødselsdato</BestillingTitle>
				<DollyFieldArray header="Fødselsdato" data={foedselsdatoListe}>
					{(foedselsdato: FoedselsdatoData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								{isEmpty(foedselsdato, ['kilde', 'master']) ? (
									<EmptyObject />
								) : (
									<>
										<TitleValue title="Fødselsdato" value={formatDate(foedselsdato.foedselsdato)} />
										<TitleValue title="Fødselsår" value={foedselsdato.foedselsaar} />
										<TitleValue title="Master" value={foedselsdato.master} />
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
