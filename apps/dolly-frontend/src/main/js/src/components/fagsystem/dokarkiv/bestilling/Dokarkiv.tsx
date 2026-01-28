import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { oversettBoolean, showLabel } from '@/utils/DataFormatter'
import { Kodeverk } from '@/components/fagsystem/dokarkiv/form/partials/Dokument'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Journalpost } from '@/utils/hooks/useDokumenter'

type DokarkivProps = {
	dokarkivListe: Journalpost[]
}

export const Dokarkiv = ({ dokarkivListe }: DokarkivProps) => {
	if (!dokarkivListe || dokarkivListe.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Dokumenter (Joark)</BestillingTitle>
				<DollyFieldArray header="Dokument" data={dokarkivListe}>
					{(dokarkiv: Journalpost, idx: number) => (
						<React.Fragment key={idx}>
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
						</React.Fragment>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
