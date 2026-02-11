import {
	arrayToString,
	formatDateTimeWithSeconds,
	getTypePerson,
	oversettBoolean,
} from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'

export const Bestillingsinformasjon = ({ bestillingsinfo }) => {
	const firstIdent =
		bestillingsinfo?.status
			?.flatMap((s: any) => s?.statuser ?? [])
			.find((sd: any) => sd?.identer?.length && sd?.melding === 'OK')?.identer?.[0] ?? null

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Bestillingsinformasjon</BestillingTitle>
				<div className="bestilling-blokk">
					<BestillingData>
						<TitleValue title="Antall bestilt" value={bestillingsinfo?.antallIdenter?.toString()} />
						<TitleValue title="Antall levert" value={bestillingsinfo?.antallLevert?.toString()} />
						<TitleValue title="Type person" value={getTypePerson(firstIdent)} />
						<TitleValue
							title="Identtype"
							value={bestillingsinfo?.bestilling?.pdldata?.opprettNyPerson?.identtype}
						/>
						<TitleValue
							title="Ny ident (2032)"
							value={oversettBoolean(bestillingsinfo?.bestilling?.pdldata?.opprettNyPerson?.id2032)}
						/>
						<TitleValue
							title="Miljøer"
							value={arrayToString(bestillingsinfo?.environments, ', ')?.toUpperCase()}
						/>
						<TitleValue
							title="Sist oppdatert"
							value={formatDateTimeWithSeconds(bestillingsinfo?.sistOppdatert)}
						/>
						<TitleValue
							title="Gjenopprettet fra"
							value={
								bestillingsinfo?.opprettetFraId
									? `Bestilling # ${bestillingsinfo.opprettetFraId}`
									: bestillingsinfo?.opprettetFraGruppeId &&
										`Gruppe # ${bestillingsinfo.opprettetFraGruppeId}`
							}
						/>
					</BestillingData>
				</div>
			</ErrorBoundary>
		</div>
	)
}
