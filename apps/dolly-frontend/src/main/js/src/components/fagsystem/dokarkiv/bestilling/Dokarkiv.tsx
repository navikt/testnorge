import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { oversettBoolean, showLabel } from '@/utils/DataFormatter'
import { Journalpost } from '@/service/services/JoarkDokumentService'
import { Kodeverk } from '@/components/fagsystem/dokarkiv/form/partials/Dokument'

type DokarkivProps = {
	dokarkiv: Journalpost
}

export const Dokarkiv = ({ dokarkiv }: DokarkivProps) => {
	if (!dokarkiv || isEmpty(dokarkiv)) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Dokumenter (Joark)</BestillingTitle>
				<BestillingData>
					<TitleValue title="Brevkode" value={dokarkiv.dokumenter?.[0]?.brevkode} />
					<TitleValue title="Tittel" value={dokarkiv.tittel} />
					<TitleValue title="Tema" value={dokarkiv.tema} kodeverk={Kodeverk.TEMA} />
					<TitleValue
						title="Behandlingstema"
						value={dokarkiv.behandlingstema}
						kodeverk={Kodeverk.BEHANDLINGSTEMA}
					/>
					<TitleValue title="Journalførende enhet" value={dokarkiv.journalfoerendeEnhet} />
					<TitleValue title="Sakstype" value={showLabel('sakstype', dokarkiv.sak?.sakstype)} />
					<TitleValue
						title="Fagsaksystem"
						value={showLabel('fagsaksystem', dokarkiv.sak?.fagsaksystem)}
					/>
					<TitleValue title="Fagsak-ID" value={dokarkiv.sak?.fagsakId} />
					<TitleValue
						title="Ferdigstill journalpost"
						value={oversettBoolean(dokarkiv.ferdigstill)}
					/>
					<TitleValue title="Avsender type" value={dokarkiv.avsenderMottaker?.idType} />
					<TitleValue title="Avsender ID" value={dokarkiv.avsenderMottaker?.id} />
					<TitleValue title="Avsender navn" value={dokarkiv.avsenderMottaker?.navn} />
					<TitleValue title="Antall vedlegg" value={dokarkiv.vedlegg?.length || '0'} />
				</BestillingData>
			</ErrorBoundary>
		</div>
	)
}
