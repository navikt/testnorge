import { useMemo, useState } from 'react'
import { Alert, BodyShort, VStack } from '@navikt/ds-react'
import DollySpinner from '@/components/ui/loading/DollySpinner'
import '@/pages/adminPages/Dashboard/dashboardHighchartsSetup'
import { MONTH_SCOPE_LAST_12 } from '@/pages/adminPages/Dashboard/dashboardUtils'
import {
	useBrukerBestillingerDetaljert,
	useBrukerBestillingerDetaljertForMaaneder,
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
	ChartSection,
	FagsystemDetaljerSection,
	PeriodeSection,
	TrendSection,
} from './brukerStatistikkSections'
import {
	dagAkse,
	filterMaanedPunkter,
	harAktivitet,
	harDagPersonData,
	maanedAkse,
	type MonthScope,
	periodeTilMaanedNavn,
	periodeVisning,
	toAarOptions,
	toAarSammendrag,
	toFagsystemDetaljer,
	toFagsystemerMedDetaljer,
	toFagsystemMatrise,
	toFagsystemSummer,
	toFagsystemSummerUtenUspesifisert,
	toMaanedOptions,
	toMaanedPunkter,
	toPeriodeTittel,
	toPersonMatrise,
	type PeriodeVisning,
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
	const [visning, setVisning] = useState<PeriodeVisning>('maaned')

	const maanedPunkter = useMemo(() => toMaanedPunkter(bestillingerOversikt), [bestillingerOversikt])
	const aktivePerioder = useMemo(() => maanedPunkter.filter(harAktivitet), [maanedPunkter])
	const aarSammendrag = useMemo(() => toAarSammendrag(maanedPunkter), [maanedPunkter])
	const valgtPunkt =
		aktivePerioder.find((punkt) => punkt.key === valgtPeriodeKey) ?? aktivePerioder.at(-1) ?? null

	const visAar = visning === 'aar'
	const valgtAarNummer = valgtPunkt?.year ?? null
	const aarMaaneder = useMemo(
		() =>
			visAar && valgtAarNummer !== null
				? aktivePerioder
						.filter((punkt) => punkt.year === valgtAarNummer)
						.map((punkt) => ({ year: punkt.year, month: periodeTilMaanedNavn(punkt) }))
				: [],
		[visAar, valgtAarNummer, aktivePerioder],
	)

	const maanedDetaljer = useBrukerBestillingerDetaljert(
		eierId,
		valgtPunkt?.year ?? null,
		valgtPunkt ? periodeTilMaanedNavn(valgtPunkt) : null,
	)
	const aarDetaljer = useBrukerBestillingerDetaljertForMaaneder(eierId, aarMaaneder)
	const { bestillingerDetaljert, loadingBestillingerDetaljert, bestillingerDetaljertError } = visAar
		? aarDetaljer
		: maanedDetaljer

	const fagsystemSummer = useMemo(
		() => toFagsystemSummer(bestillingerDetaljert),
		[bestillingerDetaljert],
	)
	const fagsystemerUtenUspesifisert = toFagsystemSummerUtenUspesifisert(fagsystemSummer)
	const tidsakse = useMemo(
		() => (valgtPunkt ? (visAar ? maanedAkse(valgtPunkt.year) : dagAkse(valgtPunkt)) : null),
		[visAar, valgtPunkt],
	)
	const personMatrise = useMemo(
		() => (tidsakse ? toPersonMatrise(bestillingerDetaljert, tidsakse) : null),
		[bestillingerDetaljert, tidsakse],
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
	const periodeTittel = toPeriodeTittel(visning, valgtPunkt.year, periodeVisning(valgtPunkt))
	const periodeNavn = visAar ? 'valgt år' : 'valgt måned'
	const inndeling = visAar ? 'per måned' : 'per dag'
	const valgDetaljer = fagsystemDetaljer.filter((detalj) => detalj.type === 'valg')
	const antallDetaljer = fagsystemDetaljer.filter((detalj) => detalj.type === 'antall')

	const onPeriodeChange = (periodeKey: string) => {
		setValgtPeriodeKey(periodeKey)
		setVisning('maaned')
	}

	const onAarChange = (aar: string) => {
		const sistePeriodeIAaret = aktivePerioder.filter((punkt) => String(punkt.year) === aar).at(-1)
		if (sistePeriodeIAaret) {
			setValgtPeriodeKey(sistePeriodeIAaret.key)
		}
	}

	return (
		<VStack gap={{ xs: 'space-16', md: 'space-24' }} className="statistikk">
			<BodyShort>
				{teamNavn
					? `Statistikk over bestillingene til teamet ${teamNavn}, som du representerer nå.`
					: 'Statistikk over bestillingene dine.'}{' '}
				Fordelingen på fagsystem gjelder bare nye bestillinger, ikke gjenopprettinger.
			</BodyShort>

			<PeriodeSection
				aarOptions={toAarOptions(aktivePerioder)}
				valgtAar={valgtAar}
				onAarChange={onAarChange}
				aarSammendrag={aarSammendrag.get(valgtPunkt.year) ?? null}
				maanedOptions={toMaanedOptions(aktivePerioder, valgtPunkt.year)}
				valgtPeriodeKey={valgtPunkt.key}
				onPeriodeChange={onPeriodeChange}
				valgtPunkt={valgtPunkt}
				visning={visning}
				onVisningChange={setVisning}
				mestBrukteFagsystem={fagsystemerUtenUspesifisert[0]?.label ?? null}
				antallFagsystemer={fagsystemerUtenUspesifisert.length}
				isLoadingDetaljert={loadingBestillingerDetaljert}
				detaljerFeil={bestillingerDetaljertError}
			>
				{!detaljerUtilgjengelige && (
					<>
						{personMatrise && (
							<ChartSection
								title={`Nye personer ${inndeling} ${periodeTittel}`}
								description="Personer fra nye bestillinger, delt på personer opprettet i Dolly og personer importert fra Testnorge."
								ariaLabel={`Nye personer ${inndeling} fordelt på opprettet i Dolly og importert fra Testnorge`}
								emptyStateMessage={`Ingen nye personer i ${periodeNavn}.`}
								harData={harDagPersonData(personMatrise)}
								isLoading={loadingBestillingerDetaljert}
								chartOptions={createDagPersonerChartOptions(
									personMatrise,
									`${inndeling} i ${periodeNavn}`,
								)}
							/>
						)}

						<ChartSection
							title={`Fagsystem ${periodeTittel}`}
							description="Antall identer bestilt mot hvert fagsystem."
							ariaLabel="Fordeling av bestilte identer per fagsystem"
							emptyStateMessage={`Ingen nye bestillinger med fagsystemdata i ${periodeNavn}.`}
							harData={fagsystemSummer.length > 0}
							isLoading={loadingBestillingerDetaljert}
							chartOptions={createFagsystemFordelingChartOptions(fagsystemSummer, periodeNavn)}
						/>

						<ChartSection
							title={`Fagsystem ${inndeling} ${periodeTittel}`}
							ariaLabel={`Bestilte identer ${inndeling} fordelt på fagsystem`}
							emptyStateMessage={`Ingen nye bestillinger med fagsystemdata i ${periodeNavn}.`}
							harData={fagsystemSummer.length > 0}
							isLoading={loadingBestillingerDetaljert}
							chartOptions={
								tidsakse
									? createFagsystemPerDagChartOptions(
											toFagsystemMatrise(bestillingerDetaljert, tidsakse),
											`${inndeling} i ${periodeNavn}`,
										)
									: {}
							}
						/>

						<FagsystemDetaljerSection
							title={`Detaljer per fagsystem ${periodeTittel}`}
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
											periodeNavn,
										)
									: null
							}
							antallChartOptions={
								antallDetaljer.length > 0 && aktivtFagsystem
									? createFagsystemDetaljerChartOptions(
											antallDetaljer,
											aktivtFagsystem.label,
											'antall',
											periodeNavn,
										)
									: null
							}
							isLoading={loadingBestillingerDetaljert}
						/>
					</>
				)}
			</PeriodeSection>

			<TrendSection
				monthScope={monthScope}
				onMonthScopeChange={setMonthScope}
				chartOptions={createBrukerTrendChartOptions(filterMaanedPunkter(maanedPunkter, monthScope))}
			/>
		</VStack>
	)
}
