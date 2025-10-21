import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'
import { showTyperLabel } from '@/components/fagsystem/arbeidssoekerregisteret/visning/ArbeidssoekerregisteretVisning'
import { ArbeidssoekerregisteretTypes } from '@/components/fagsystem/arbeidssoekerregisteret/arbeidssoekerregisteretTypes'

type ArbeidssoekerregisteretProps = {
	arbeidssoekerregisteret: ArbeidssoekerregisteretTypes
}

export const Arbeidssoekerregisteret = ({
	arbeidssoekerregisteret,
}: ArbeidssoekerregisteretProps) => {
	if (!arbeidssoekerregisteret || isEmpty(arbeidssoekerregisteret)) {
		return null
	}

	const jobbsituasjonsdetaljer = arbeidssoekerregisteret.jobbsituasjonsdetaljer

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Arbeidssøkerregisteret</BestillingTitle>
				<BestillingData>
					<TitleValue
						title="Utført av"
						value={showTyperLabel('BRUKERTYPE', arbeidssoekerregisteret.utfoertAv)}
					/>
					<TitleValue title="Kilde" value={arbeidssoekerregisteret.kilde} />
					<TitleValue title="Årsak" value={arbeidssoekerregisteret.aarsak} />
					<TitleValue
						title="Utdanningsnivå"
						value={showTyperLabel('NUSKODE', arbeidssoekerregisteret.nuskode)}
					/>
					<TitleValue
						title="Beskrivelse av jobbsituasjonen"
						value={showTyperLabel(
							'JOBBSITUASJONSBESKRIVELSE',
							arbeidssoekerregisteret.jobbsituasjonsbeskrivelse,
						)}
					/>
					<TitleValue
						title="Utdanning bestått"
						value={oversettBoolean(arbeidssoekerregisteret.utdanningBestaatt)}
					/>
					<TitleValue
						title="Utdanning godkjent"
						value={oversettBoolean(arbeidssoekerregisteret.utdanningGodkjent)}
					/>
					<TitleValue
						title="Helsetilstand hindrer arbeid"
						value={oversettBoolean(arbeidssoekerregisteret.helsetilstandHindrerArbeid)}
					/>
					<TitleValue
						title="Andre forhold hindrer arbeid"
						value={oversettBoolean(arbeidssoekerregisteret.andreForholdHindrerArbeid)}
					/>
				</BestillingData>
				{jobbsituasjonsdetaljer && !isEmpty(jobbsituasjonsdetaljer) && (
					<>
						<h3>Detaljer om jobbsituasjonen</h3>
						<BestillingData>
							<TitleValue
								title="Gjelder fra dato"
								value={formatDate(jobbsituasjonsdetaljer?.gjelderFraDato)}
							/>
							<TitleValue
								title="Gjelder til dato"
								value={formatDate(jobbsituasjonsdetaljer?.gjelderTilDato)}
							/>
							<TitleValue title="Stilling" value={jobbsituasjonsdetaljer?.stillingstittel} />
							<TitleValue
								title="Stillingsprosent"
								value={jobbsituasjonsdetaljer?.stillingsprosent}
							/>
							<TitleValue
								title="Siste dag med lønn"
								value={formatDate(jobbsituasjonsdetaljer?.sisteDagMedLoenn)}
							/>
							<TitleValue
								title="Siste arbeidsdag"
								value={formatDate(jobbsituasjonsdetaljer?.sisteArbeidsdag)}
							/>
						</BestillingData>
					</>
				)}
			</ErrorBoundary>
		</div>
	)
}
