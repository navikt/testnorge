import { useMemo, useState } from 'react'
import { Alert, BodyShort, VStack } from '@navikt/ds-react'
import DollySpinner from '@/components/ui/loading/DollySpinner'
import '@/pages/adminPages/Dashboard/dashboardHighchartsSetup'
import { MONTH_SCOPE_LAST_12 } from '@/pages/adminPages/Dashboard/dashboardUtils'
import {
	useBrukerBestillingerDetaljert,
	useBrukerBestillingerOversikt,
} from '@/utils/hooks/useBrukerStatistikk'
import {
	createBrukerTrendChartOptions,
	createDagPersonerChartOptions,
	createFagsystemDetaljerChartOptions,
	createFagsystemFordelingChartOptions,
	createFagsystemPerDagChartOptions,
} from './brukerStatistikkChartOptions'
import {
	AarSection,
	ChartSection,
	FagsystemDetaljerSection,
	MaanedSection,
	TrendSection,
} from './brukerStatistikkSections'
import {
	aarSammendragTittel,
	filterMaanedPunkter,
	harAktivitet,
	harDagPersonData,
	type MonthScope,
	periodeTilMaanedNavn,
	periodeVisningITekst,
	toAarOptions,
	toAarSammendrag,
	toDagFagsystemMatrise,
	toDagPersonMatrise,
	toFagsystemDetaljer,
	toFagsystemerMedDetaljer,
	toFagsystemSummer,
	toFagsystemSummerUtenUspesifisert,
	toMaanedOptions,
	toMaanedPunkter,
} from './brukerStatistikkUtils'

interface BrukerStatistikkProps {
	eierId?: string
	teamNavn?: string
}

export const BrukerStatistikk = ({ eierId, teamNavn }: BrukerStatistikkProps) => {
	const { bestillingerOversikt, loadingBestillingerOversikt, bestillingerOversiktError } =
		useBrukerBestillingerOversikt(eierId)
	const [valgtPeriodeKey, setValgtPeriodeKey] = useState<string | null>(null)
	const [valgtFagsystem, setValgtFagsystem] = useState<string | null>(null)
	const [monthScope, setMonthScope] = useState<MonthScope>(MONTH_SCOPE_LAST_12)

	const maanedPunkter = useMemo(() => toMaanedPunkter(bestillingerOversikt), [bestillingerOversikt])
	const aktivePerioder = useMemo(() => maanedPunkter.filter(harAktivitet), [maanedPunkter])
	const aarSammendrag = useMemo(() => toAarSammendrag(maanedPunkter), [maanedPunkter])
	const valgtPunkt =
		aktivePerioder.find((punkt) => punkt.key === valgtPeriodeKey) ?? aktivePerioder.at(-1) ?? null

	const { bestillingerDetaljert, loadingBestillingerDetaljert, bestillingerDetaljertError } =
		useBrukerBestillingerDetaljert(
			eierId,
			valgtPunkt?.year ?? null,
			valgtPunkt ? periodeTilMaanedNavn(valgtPunkt) : null,
		)

	const fagsystemSummer = useMemo(
		() => toFagsystemSummer(bestillingerDetaljert),
		[bestillingerDetaljert],
	)
	const fagsystemerUtenUspesifisert = toFagsystemSummerUtenUspesifisert(fagsystemSummer)
	const dagPersonMatrise = useMemo(
		() => (valgtPunkt ? toDagPersonMatrise(bestillingerDetaljert, valgtPunkt) : null),
		[bestillingerDetaljert, valgtPunkt],
	)
	const fagsystemerMedDetaljer = useMemo(
		() => toFagsystemerMedDetaljer(bestillingerDetaljert),
		[bestillingerDetaljert],
	)
	const aktivtFagsystem =
		fagsystemerMedDetaljer.find((sum) => sum.fagsystem === valgtFagsystem) ??
		fagsystemerMedDetaljer[0] ??
		null
	const fagsystemDetaljer = aktivtFagsystem
		? toFagsystemDetaljer(bestillingerDetaljert, aktivtFagsystem.fagsystem)
		: []

	if (loadingBestillingerOversikt) {
		return <DollySpinner size={120} label="Laster statistikk..." />
	}

	if (bestillingerOversiktError) {
		return (
			<Alert variant="error">
				Klarte ikke å hente statistikk: {bestillingerOversiktError.message}
			</Alert>
		)
	}

	if (!valgtPunkt) {
		return (
			<Alert variant="info">
				{teamNavn
					? `Teamet ${teamNavn} har ingen bestillinger enda. Statistikken vises her når teamet har bestilt identer.`
					: 'Du har ingen bestillinger enda. Statistikken vises her når du har bestilt identer.'}
			</Alert>
		)
	}

	const valgtAar = String(valgtPunkt.year)
	const detaljerUtilgjengelige = Boolean(bestillingerDetaljertError)
	const valgtMaanedTekst = periodeVisningITekst(valgtPunkt)
	const valgDetaljer = fagsystemDetaljer.filter((detalj) => detalj.type === 'valg')
	const antallDetaljer = fagsystemDetaljer.filter((detalj) => detalj.type === 'antall')

	const onAarChange = (aar: string) => {
		const sistePeriodeIAaret = aktivePerioder.filter((punkt) => String(punkt.year) === aar).at(-1)
		if (sistePeriodeIAaret) {
			setValgtPeriodeKey(sistePeriodeIAaret.key)
		}
	}

	return (
		<VStack gap={{ xs: 'space-16', md: 'space-24' }}>
			<BodyShort>
				{teamNavn
					? `Statistikk over bestillingene til teamet ${teamNavn}, som du representerer nå.`
					: 'Statistikk over bestillingene dine.'}{' '}
				Fordelingen på fagsystem gjelder bare nye bestillinger, ikke gjenopprettinger.
			</BodyShort>

			<AarSection
				aarOptions={toAarOptions(aktivePerioder)}
				valgtAar={valgtAar}
				onAarChange={onAarChange}
				aarSammendrag={aarSammendrag.get(valgtPunkt.year) ?? null}
				aarSammendragTittel={aarSammendragTittel(valgtPunkt.year)}
			>
				<MaanedSection
					maanedOptions={toMaanedOptions(aktivePerioder, valgtPunkt.year)}
					valgtPeriodeKey={valgtPunkt.key}
					onPeriodeChange={setValgtPeriodeKey}
					valgtPunkt={valgtPunkt}
					valgtMaanedTekst={valgtMaanedTekst}
					mestBrukteFagsystem={fagsystemerUtenUspesifisert[0]?.label ?? null}
					antallFagsystemer={fagsystemerUtenUspesifisert.length}
					isLoadingDetaljert={loadingBestillingerDetaljert}
					detaljerFeil={bestillingerDetaljertError}
				>
					{!detaljerUtilgjengelige && (
						<>
							{dagPersonMatrise && (
								<ChartSection
									title={`Nye personer per dag i ${valgtMaanedTekst}`}
									description="Personer fra nye bestillinger, delt på personer opprettet i Dolly og personer importert fra Testnorge."
									ariaLabel="Nye personer per dag fordelt på opprettet i Dolly og importert fra Testnorge"
									emptyStateMessage="Ingen nye personer i valgt måned."
									harData={harDagPersonData(dagPersonMatrise)}
									isLoading={loadingBestillingerDetaljert}
									chartOptions={createDagPersonerChartOptions(dagPersonMatrise)}
								/>
							)}

							<ChartSection
								title={`Fagsystem i ${valgtMaanedTekst}`}
								description="Antall identer bestilt mot hvert fagsystem."
								ariaLabel="Fordeling av bestilte identer per fagsystem"
								emptyStateMessage="Ingen nye bestillinger med fagsystemdata i valgt måned."
								harData={fagsystemSummer.length > 0}
								isLoading={loadingBestillingerDetaljert}
								chartOptions={createFagsystemFordelingChartOptions(fagsystemSummer)}
							/>

							<ChartSection
								title={`Fagsystem per dag i ${valgtMaanedTekst}`}
								ariaLabel="Bestilte identer per dag fordelt på fagsystem"
								emptyStateMessage="Ingen nye bestillinger med fagsystemdata i valgt måned."
								harData={fagsystemSummer.length > 0}
								isLoading={loadingBestillingerDetaljert}
								chartOptions={createFagsystemPerDagChartOptions(
									toDagFagsystemMatrise(bestillingerDetaljert, valgtPunkt),
								)}
							/>

							<FagsystemDetaljerSection
								title={`Detaljer per fagsystem i ${valgtMaanedTekst}`}
								fagsystemOptions={fagsystemerMedDetaljer.map((sum) => ({
									value: sum.fagsystem,
									label: sum.label,
								}))}
								valgtFagsystem={aktivtFagsystem?.fagsystem ?? null}
								onFagsystemChange={setValgtFagsystem}
								valgtFagsystemLabel={aktivtFagsystem?.label ?? ''}
								valgChartOptions={
									valgDetaljer.length > 0 && aktivtFagsystem
										? createFagsystemDetaljerChartOptions(
												valgDetaljer,
												aktivtFagsystem.label,
												'valg',
											)
										: null
								}
								antallChartOptions={
									antallDetaljer.length > 0 && aktivtFagsystem
										? createFagsystemDetaljerChartOptions(
												antallDetaljer,
												aktivtFagsystem.label,
												'antall',
											)
										: null
								}
								isLoading={loadingBestillingerDetaljert}
							/>
						</>
					)}
				</MaanedSection>
			</AarSection>

			<TrendSection
				monthScope={monthScope}
				onMonthScopeChange={setMonthScope}
				chartOptions={createBrukerTrendChartOptions(filterMaanedPunkter(maanedPunkter, monthScope))}
			/>
		</VStack>
	)
}
