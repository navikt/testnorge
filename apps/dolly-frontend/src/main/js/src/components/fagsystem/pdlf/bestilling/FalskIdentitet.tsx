import { FalskIdentitetData } from '@/components/fagsystem/pdlf/PdlTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showKodeverkLabel } from '@/utils/DataFormatter'
import * as _ from 'lodash-es'
import { AdresseKodeverk } from '@/config/kodeverk'

type FalskIdentitetTypes = {
	falskIdentitetListe: Array<FalskIdentitetData>
}

export const FalskIdentitet = ({ falskIdentitetListe }: FalskIdentitetTypes) => {
	if (!falskIdentitetListe || falskIdentitetListe.length < 1) {
		return null
	}

	const sjekkRettIdent = (item: FalskIdentitetData) => {
		if (_.has(item, 'rettIdentitetErUkjent')) {
			return 'Ukjent'
		} else if (_.has(item, 'rettIdentitetVedIdentifikasjonsnummer')) {
			return 'Ved identifikasjonsnummer'
		}
		return _.has(item, 'rettIdentitetVedOpplysninger') ? 'Ved personopplysninger' : 'Ingen'
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Falsk identitet</BestillingTitle>
				<DollyFieldArray header="Falsk identitet" data={falskIdentitetListe}>
					{(falskIdentitet: FalskIdentitetData, idx: number) => {
						const statsborgerskapFormatted =
							falskIdentitet.rettIdentitetVedOpplysninger?.statsborgerskap
								?.map((land) => {
									return showKodeverkLabel(AdresseKodeverk.StatsborgerskapLand, land)
								})
								?.join(', ')

						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Opplysninger om rett ident"
									value={sjekkRettIdent(falskIdentitet)}
								/>
								<TitleValue
									title="Identifikasjonsnummer"
									value={falskIdentitet.rettIdentitetVedIdentifikasjonsnummer}
								/>
								<TitleValue
									title="Fornavn"
									value={falskIdentitet.rettIdentitetVedOpplysninger?.personnavn?.fornavn}
								/>
								<TitleValue
									title="Mellomnavn"
									value={falskIdentitet.rettIdentitetVedOpplysninger?.personnavn?.mellomnavn}
								/>
								<TitleValue
									title="Etternavn"
									value={falskIdentitet.rettIdentitetVedOpplysninger?.personnavn?.etternavn}
								/>
								<TitleValue
									title="Fødselsdato"
									value={formatDate(falskIdentitet.rettIdentitetVedOpplysninger?.foedselsdato)}
								/>
								<TitleValue
									title="Kjønn"
									value={falskIdentitet.rettIdentitetVedOpplysninger?.kjoenn}
								/>
								<TitleValue title="Statsborgerskap" value={statsborgerskapFormatted} />
								<TitleValue title="Master" value={falskIdentitet.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
