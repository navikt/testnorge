import { UtenlandskIdentData } from '@/components/fagsystem/pdlf/PdlTypes'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { oversettBoolean } from '@/utils/DataFormatter'
import { AdresseKodeverk } from '@/config/kodeverk'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'

type UtenlandskIdentTypes = {
	utenlandskIdentListe: Array<UtenlandskIdentData>
}

export const UtenlandskIdent = ({ utenlandskIdentListe }: UtenlandskIdentTypes) => {
	if (!utenlandskIdentListe || utenlandskIdentListe.length < 1) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Utenlandsk identifikasjonsnummer</BestillingTitle>
				<DollyFieldArray header="Utenlandsk ID" data={utenlandskIdentListe}>
					{(utenlandskIdent: UtenlandskIdentData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="Utenlandsk ID" value={utenlandskIdent.identifikasjonsnummer} />
								<TitleValue
									title="Utstederland"
									value={utenlandskIdent.utstederland}
									kodeverk={AdresseKodeverk.Utstederland}
								/>
								<TitleValue
									title="Utenlandsk ID opphørt"
									value={oversettBoolean(utenlandskIdent.opphoert)}
								/>
								<TitleValue title="Master" value={utenlandskIdent.master} />
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
