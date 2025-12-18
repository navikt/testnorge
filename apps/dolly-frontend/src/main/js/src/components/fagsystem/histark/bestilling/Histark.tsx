import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, formatDateTime, showKodeverkLabel } from '@/utils/DataFormatter'
import React from 'react'
import { HistarkTypes } from '@/components/fagsystem/histark/HistarkTypes'
import { Kodeverk } from '@/components/fagsystem/dokarkiv/form/partials/Dokument'

type HistarkProps = {
	histark: HistarkTypes
}

export const Histark = ({ histark }: HistarkProps) => {
	if (!histark || isEmpty(histark)) {
		return null
	}

	const dokument = histark.dokumenter?.[0]

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Dokumenter (Histark)</BestillingTitle>
				<div className="bestilling-blokk">
					<BestillingData>
						<TitleValue
							title="Temakoder"
							value={arrayToString(
								dokument?.temakoder?.map((kode) => showKodeverkLabel(Kodeverk.TEMA, kode)),
							)}
						/>
						<TitleValue title="Nav-enhet" value={dokument?.enhetsnavn} />
						<TitleValue title="Startår" value={dokument?.startYear} />
						<TitleValue title="Sluttår" value={dokument?.endYear} />
						<TitleValue
							title="Skanningstidspunkt"
							value={formatDateTime(dokument?.skanningsTidspunkt)}
						/>
						<TitleValue title="Skanner" value={dokument?.skanner} />
						<TitleValue title="Skannested" value={dokument?.skannested} />
						<TitleValue title="Vedlegg tittel" value={dokument?.tittel} />
					</BestillingData>
				</div>
			</ErrorBoundary>
		</div>
	)
}
