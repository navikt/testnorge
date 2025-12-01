import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import {
	formatDate,
	oversettBoolean,
	showLabel,
	uppercaseAndUnderscoreToCapitalized,
} from '@/utils/DataFormatter'
import React from 'react'
import { ArenaTypes } from '@/components/fagsystem/arena/ArenaTypes'

type ArenaProps = {
	arbeidsytelse: ArenaTypes
}

export const Arena = ({ arbeidsytelse }: ArenaProps) => {
	if (!arbeidsytelse || isEmpty(arbeidsytelse)) {
		return null
	}

	const aap115 = arbeidsytelse.aap115?.[0]
	const aap = arbeidsytelse.aap?.[0]
	const dagpenger = arbeidsytelse.dagpenger?.[0]

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Arbeidsytelser</BestillingTitle>
				<div className="bestilling-blokk">
					<BestillingData>
						<TitleValue
							title="Brukertype"
							value={uppercaseAndUnderscoreToCapitalized(arbeidsytelse.arenaBrukertype)}
						/>
						<TitleValue
							title="Servicebehov"
							value={showLabel('kvalifiseringsgruppe', arbeidsytelse.kvalifiseringsgruppe)}
						/>
						<TitleValue
							title="Inaktiv fra dato"
							value={formatDate(arbeidsytelse.inaktiveringDato)}
						/>
						<TitleValue title="Aktiveringsdato" value={formatDate(arbeidsytelse.aktiveringDato)} />
						<TitleValue
							title="Automatisk innsending av meldekort"
							value={oversettBoolean(arbeidsytelse.automatiskInnsendingAvMeldekort)}
						/>
					</BestillingData>
					{aap115 && (
						<>
							<BestillingTitle>11-5-vedtak</BestillingTitle>
							<BestillingData>
								<TitleValue title="Fra dato" value={formatDate(aap115.fraDato)} />
								<TitleValue title="Til dato" value={formatDate(aap115.tilDato)} />
							</BestillingData>
						</>
					)}
					{aap && (
						<>
							<BestillingTitle>AAP-vedtak</BestillingTitle>
							<BestillingData>
								<TitleValue title="Fra dato" value={formatDate(aap.fraDato)} />
								<TitleValue title="Til dato" value={formatDate(aap.tilDato)} />
							</BestillingData>
						</>
					)}
					{dagpenger && (
						<>
							<BestillingTitle>Dagpengevedtak</BestillingTitle>
							<BestillingData>
								<TitleValue
									title="Rettighetskode"
									value={showLabel('rettighetKode', dagpenger.rettighetKode)}
								/>
								<TitleValue title="Fra dato" value={formatDate(dagpenger.fraDato)} />
								<TitleValue title="Til dato" value={formatDate(dagpenger.tilDato)} />
								<TitleValue title="Mottatt dato" value={formatDate(dagpenger.mottattDato)} />
							</BestillingData>
						</>
					)}
				</div>
			</ErrorBoundary>
		</div>
	)
}
