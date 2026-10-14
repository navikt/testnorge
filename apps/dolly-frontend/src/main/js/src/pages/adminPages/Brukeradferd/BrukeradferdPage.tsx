import { Alert, BodyShort, VStack } from '@navikt/ds-react'
import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import { DashboardHeaderActions } from '@/pages/adminPages/Dashboard/dashboardSharedComponents'
import '@/pages/adminPages/Dashboard/dashboardHighchartsSetup'
import '@/pages/statistikk/Statistikk.less'
import { useErDollyAdmin } from '@/utils/DollyAdmin'
import {
	AdferdOverTidSection,
	BrukeradferdPanel,
	FagsystemDetaljerSection,
	FagsystemFordelingSection,
	NyePersonerSection,
	ValgtPunktSection,
} from './brukeradferdSections'
import { useBrukeradferdData } from './useBrukeradferdData'

const erDevDashboardMode = () =>
	typeof window !== 'undefined' &&
	(window.location.hostname.includes('localhost') ||
		window.location.hostname.includes('dolly-frontend-dev'))

const BrukeradferdInnhold = () => {
	const data = useBrukeradferdData()

	return (
		<>
			<h1>Brukeradferd</h1>
			<VStack gap={{ xs: 'space-16', md: 'space-24' }} className="statistikk">
				<BodyShort>
					Statistikk over bestillinger fra alle Dolly-brukere. Tallene gjelder bare nye
					bestillinger, ikke gjenopprettinger. En bestilling kan ha flere fagsystemer, så summen av
					fagsystemvalg blir høyere enn antall nye personer.
				</BodyShort>

				{erDevDashboardMode() && (
					<DashboardHeaderActions
						mockModeEnabled={data.mockModeEnabled}
						onSeedMockData={data.onSeedMockData}
						onShowRealData={data.onShowRealData}
					/>
				)}

				{data.oversiktError && (
					<Alert variant="error">
						Klarte ikke å hente tilgjengelige perioder: {data.oversiktError.message}
					</Alert>
				)}
				{data.brukeradferdError && (
					<Alert variant="error">
						Klarte ikke å hente brukeradferd: {data.brukeradferdError.message}
					</Alert>
				)}

				<BrukeradferdPanel
					yearOptions={data.yearOptions}
					selectedYear={data.selectedYear}
					onYearChange={data.onYearChange}
					monthOptions={data.monthOptions}
					selectedInterval={data.selectedInterval}
					onIntervalChange={data.onIntervalChange}
					visning={data.visning}
					onVisningChange={data.onVisningChange}
					noekkeltall={data.noekkeltall}
					personNoekkeltall={data.personNoekkeltall}
					isLoading={data.loadingPerioder}
					isLoadingNoekkeltall={data.loadingAdferd}
				>
					<NyePersonerSection
						punkter={data.personTidPunkter}
						granularitet={data.granularitet}
						chartOptions={data.nyePersonerChartOptions}
						periodeTittel={data.periodeTittel}
						isLoading={data.loadingAdferd}
					/>
					<FagsystemFordelingSection
						fagsystemTotaler={data.fagsystemTotaler}
						chartOptions={data.fordelingChartOptions}
						periodeTittel={data.periodeTittel}
						isLoading={data.loadingAdferd}
					/>
					<AdferdOverTidSection
						harTidsdata={data.harTidsdata}
						granularitet={data.granularitet}
						chartOptions={data.overTidChartOptions}
						periodeTittel={data.periodeTittel}
						isLoading={data.loadingAdferd}
					/>
					{data.valgtPunkt !== null && !data.loadingAdferd && (
						<ValgtPunktSection
							granularitet={data.granularitet}
							valgtPunkt={data.valgtPunkt}
							valgtPunktVisning={data.valgtPunktVisning}
							valgtPunktTotaler={data.valgtPunktTotaler}
							valgtPunktPersoner={data.valgtPunktPersoner}
							onLukk={data.onLukkValgtPunkt}
						/>
					)}
					<FagsystemDetaljerSection
						fagsystemOptions={data.fagsystemOptions}
						valgtFagsystem={data.valgtFagsystem}
						onFagsystemChange={data.onFagsystemChange}
						valgtFagsystemLabel={data.valgtFagsystemLabel}
						valgChartOptions={data.detaljValgChartOptions}
						antallChartOptions={data.detaljAntallChartOptions}
						periodeTittel={data.periodeTittel}
						isLoading={data.loadingAdferd}
					/>
				</BrukeradferdPanel>
			</VStack>
		</>
	)
}

export default () => {
	const isAdmin = useErDollyAdmin()

	if (!isAdmin) {
		return <AdminAccessDenied />
	}

	return <BrukeradferdInnhold />
}
