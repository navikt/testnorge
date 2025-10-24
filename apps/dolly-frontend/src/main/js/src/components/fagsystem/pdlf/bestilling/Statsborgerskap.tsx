import { StatsborgerskapData } from '@/components/fagsystem/pdlf/PdlTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'

type StatsborgerskapTypes = {
	statsborgerskapListe: Array<StatsborgerskapData>
}

export const Statsborgerskap = ({ statsborgerskapListe }: StatsborgerskapTypes) => {
	if (!statsborgerskapListe || statsborgerskapListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Statsborgerskap</BestillingTitle>
				<DollyFieldArray header="Statsborgerskap" data={statsborgerskapListe}>
					{(statsborgerskap: StatsborgerskapData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue
									title="Statsborgerskap"
									value={statsborgerskap.landkode}
									kodeverk={AdresseKodeverk.StatsborgerskapLand}
								/>
								<TitleValue
									title="Statsborgerskap fra"
									value={formatDate(statsborgerskap.gyldigFraOgMed)}
								/>
								<TitleValue
									title="Statsborgerskap til"
									value={formatDate(statsborgerskap.gyldigTilOgMed)}
								/>
								<TitleValue
									title="Bekreftelsesdato"
									value={formatDate(statsborgerskap.bekreftelsesdato)}
								/>
								<TitleValue title="Master" value={statsborgerskap.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
