import React from 'react'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import { Data } from '@/components/fagsystem/krrstub/visning/KrrVisning'

type KrrProps = {
	krrstub: Data
}

export const Krrstub = ({ krrstub }: KrrProps) => {
	if (!krrstub) {
		return null
	}

	const leverandoer = SelectOptionsOppslag.hentKrrLeverandoerer()

	const leverandoerLabel = () =>
		leverandoer?.value?.data?.find((leverandoer: any) => leverandoer.id === krrstub.sdpLeverandoer)
			?.navn || krrstub.sdpLeverandoer

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Kontakt- og reservasjonsregisteret</BestillingTitle>
				<div className="bestilling-blokk">
					<BestillingData>
						<TitleValue title="Registrert i KRR" value={oversettBoolean(krrstub.registrert)} />
						<TitleValue title="Reservert" value={oversettBoolean(krrstub.reservert)} />
						<TitleValue title="E-post" value={krrstub.epost} />
						<TitleValue
							title="Mobilnummer"
							value={krrstub.registrert && krrstub.mobil && `${krrstub.landkode} ${krrstub.mobil}`}
						/>
						<TitleValue title="Språk" value={showLabel('spraaktype', krrstub.spraak)} />
						<TitleValue title="Gyldig fra" value={formatDate(krrstub.gyldigFra)} />
						<TitleValue title="Adresse" value={krrstub.sdpAdresse} />
						<TitleValue title="Leverandør" value={leverandoerLabel()} />
					</BestillingData>
				</div>
			</ErrorBoundary>
		</div>
	)
}
