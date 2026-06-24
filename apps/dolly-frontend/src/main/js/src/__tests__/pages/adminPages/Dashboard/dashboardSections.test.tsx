import { render, screen } from '@testing-library/react'
import { vi } from 'vitest'
import type { ReactNode } from 'react'
import { MonthlyTeamTrendSection } from '@/pages/adminPages/Dashboard/dashboardMonthlySections'
import {
	PersonAnalysisSection,
	PreviousDaySection,
} from '@/pages/adminPages/Dashboard/dashboardDayPersonSections'
import { MONTH_SCOPE_LAST_12 } from '@/pages/adminPages/Dashboard/dashboardUtils'
import { useDashboardPerson } from '@/pages/adminPages/Dashboard/DashboardPersonContext'

vi.mock('@/pages/adminPages/Dashboard/dashboardSharedComponents', () => ({
	DashboardChartPanel: ({ ariaLabel }: { ariaLabel: string }) => <div>{ariaLabel}</div>,
	DashboardKpiCard: ({ label, value }: { label: string; value: ReactNode }) => (
		<div>
			<div>{label}</div>
			<div>{value}</div>
		</div>
	),
	DashboardSectionCard: ({ children }: { children: ReactNode }) => <section>{children}</section>,
	DashboardSelectButtons: ({
		label,
		options,
		selected,
		onSelect,
	}: {
		label?: string
		options: { value: string; label: string }[]
		selected: string | null
		onSelect: (value: string) => void
	}) => (
		<div>
			{label && <div>{label}</div>}
			{options.map((option) => (
				<button
					key={option.value}
					aria-pressed={selected === option.value}
					onClick={() => onSelect(option.value)}
				>
					{option.label}
				</button>
			))}
		</div>
	),
}))

vi.mock('@/pages/adminPages/Dashboard/DashboardPersonContext', () => ({
	useDashboardPerson: vi.fn(),
}))

describe('dashboard sections', () => {
	it('should render monthly trend section for custom panel titles', () => {
		render(
			<MonthlyTeamTrendSection
				title="Organisasjoner over tid"
				description="Viser organisasjonsdata per måned"
				filteredMonthlyTeamPointsLength={1}
				monthScope={MONTH_SCOPE_LAST_12}
				onMonthScopeChange={vi.fn()}
				monthlyTrendChartOptions={{}}
			/>,
		)

		expect(screen.getByRole('heading', { name: 'Organisasjoner over tid' })).toBeInTheDocument()
		expect(screen.getByText('Viser organisasjonsdata per måned')).toBeInTheDocument()
		expect(screen.getByText('Månedlig trend for unike brukere og antall teams')).toBeInTheDocument()
	})

	it('should render custom empty state message for monthly trend sections', () => {
		render(
			<MonthlyTeamTrendSection
				title="Ekstern bruk over tid"
				emptyStateMessage="Ingen organisasjonsdata tilgjengelig."
				filteredMonthlyTeamPointsLength={0}
				monthScope={MONTH_SCOPE_LAST_12}
				onMonthScopeChange={vi.fn()}
				monthlyTrendChartOptions={{}}
			/>,
		)

		expect(screen.getByText('Ingen organisasjonsdata tilgjengelig.')).toBeInTheDocument()
		expect(screen.queryByText('Ingen teamdata tilgjengelig.')).not.toBeInTheDocument()
	})

	it('should render person KPI cards including bestillinger and identity counts', () => {
		vi.mocked(useDashboardPerson).mockReturnValue({
			selectedQuickRange: 'month',
			fraDato: '2026-06-01',
			tilDato: '2026-06-22',
			todayDate: '2026-06-22',
			onFraDatoChange: vi.fn(),
			onTilDatoChange: vi.fn(),
			onQuickRangeClick: vi.fn(),
			filteredPersonerLength: 12,
			summary: {
				bestillinger: 34,
				personerTotalt: 20,
				nye: 12,
				gjenopprettede: 8,
				navIdenter: 19,
				testnorgeIdenter: 15,
			},
			personTrendDataLength: 12,
			personTrendChartOptions: {},
		})

		render(<PersonAnalysisSection />)

		expect(screen.getByRole('heading', { name: 'Statistikk for perioder' })).toBeInTheDocument()
		expect(screen.getByText('Bestillinger')).toBeInTheDocument()
		expect(screen.getByText('NAV-identer')).toBeInTheDocument()
		expect(screen.getByText('Testnorge-identer')).toBeInTheDocument()
	})

	it('should render person KPI and chart for the selected day period', () => {
		render(
			<PreviousDaySection
				selectedDayDisplayLabel="05.06.2026–07.06.2026"
				selectedDayPeriodTitle="Siste hverdag + helg"
				selectedDayButtonLabel="Siste hverdag + helg"
				selectedDayScope="YESTERDAY"
				onSelectedDayScopeChange={vi.fn()}
				previousDayPeriodData={[
					{
						dato: '2026-06-05',
						bestillinger: 3,
						personerTotalt: 2,
						nye: 1,
						gjenopprettede: 1,
						navIdenter: 2,
						testnorgeIdenter: 1,
					},
					{
						dato: '2026-06-06',
						bestillinger: 2,
						personerTotalt: 1,
						nye: 1,
						gjenopprettede: 0,
						navIdenter: 1,
						testnorgeIdenter: 1,
					},
					{
						dato: '2026-06-07',
						bestillinger: 2,
						personerTotalt: 1,
						nye: 1,
						gjenopprettede: 0,
						navIdenter: 1,
						testnorgeIdenter: 1,
					},
				]}
				previousDaySummary={{
					nye: 3,
					gjenopprettede: 1,
					nyeInklGjenopprettede: 4,
				}}
				previousDayChartOptions={{}}
				selectedDayFeilGrupper={[]}
				selectedDayFeilCount={0}
				loadingSelectedDayFeil={false}
			/>,
		)

		expect(
			screen.getByRole('heading', { name: /Statistikk for siste hverdag \+ helg/ }),
		).toBeInTheDocument()
		expect(screen.getByRole('button', { name: 'Siste hverdag + helg' })).toBeInTheDocument()
		expect(screen.getByText('Personer opprettet/gjenopprettet')).toBeInTheDocument()
		expect(screen.getByText('Feil totalt')).toBeInTheDocument()
		expect(screen.getByText('Opprettet og gjenopprettet for valgt dag')).toBeInTheDocument()
		expect(screen.getByText('Ingen feil registrert for valgt periode.')).toBeInTheDocument()
	})

	it('should render single-day label on non-monday days', () => {
		render(
			<PreviousDaySection
				selectedDayDisplayLabel="08.06.2026"
				selectedDayPeriodTitle="Siste hverdag"
				selectedDayButtonLabel="Siste hverdag"
				selectedDayScope="YESTERDAY"
				onSelectedDayScopeChange={vi.fn()}
				previousDayPeriodData={[
					{
						dato: '2026-06-08',
						bestillinger: 4,
						personerTotalt: 4,
						nye: 3,
						gjenopprettede: 1,
						navIdenter: 3,
						testnorgeIdenter: 1,
					},
				]}
				previousDaySummary={{
					nye: 3,
					gjenopprettede: 1,
					nyeInklGjenopprettede: 4,
				}}
				previousDayChartOptions={{}}
				selectedDayFeilGrupper={[]}
				selectedDayFeilCount={0}
				loadingSelectedDayFeil={false}
			/>,
		)

		expect(
			screen.getByRole('heading', { name: /Statistikk for siste hverdag/ }),
		).toBeInTheDocument()
		expect(screen.getByRole('button', { name: 'Siste hverdag' })).toBeInTheDocument()
	})

	it('should render feil groups for the selected day period', () => {
		render(
			<PreviousDaySection
				selectedDayDisplayLabel="08.06.2026"
				selectedDayPeriodTitle="Siste hverdag"
				selectedDayButtonLabel="Siste hverdag"
				selectedDayScope="YESTERDAY"
				onSelectedDayScopeChange={vi.fn()}
				previousDayPeriodData={[
					{
						dato: '2026-06-08',
						bestillinger: 4,
						personerTotalt: 4,
						nye: 3,
						gjenopprettede: 1,
						navIdenter: 3,
						testnorgeIdenter: 1,
					},
				]}
				previousDaySummary={{
					nye: 3,
					gjenopprettede: 1,
					nyeInklGjenopprettede: 4,
				}}
				previousDayChartOptions={{}}
				selectedDayFeilGrupper={[
					{
						feilNokkel: 'pdlForvalterFeil',
						label: 'PDL Forvalter',
						rader: [
							{
								ident: '12345678901',
								bestillingId: 1,
								sistOppdatert: '2026-06-08T10:00:00',
								master: 'PDL',
								verdi: 'Teknisk feil mot PDL',
							},
						],
					},
				]}
				selectedDayFeilCount={1}
				loadingSelectedDayFeil={false}
			/>,
		)

		expect(screen.getByRole('heading', { name: 'Feil registrert 08.06.2026' })).toBeInTheDocument()
		expect(screen.getByText('PDL Forvalter (1)')).toBeInTheDocument()
		expect(
			screen.getByText('1 bestilling(er) med feil, fordelt over 1 fagsystem.'),
		).toBeInTheDocument()
	})
})
