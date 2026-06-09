import { render, screen } from '@testing-library/react'
import { vi } from 'vitest'
import type { ReactNode } from 'react'
import { MonthlyTeamTrendSection } from '@/pages/adminPages/Dashboard/dashboardMonthlySections'
import { PreviousDaySection } from '@/pages/adminPages/Dashboard/dashboardDayPersonSections'
import { MONTH_SCOPE_LAST_12 } from '@/pages/adminPages/Dashboard/dashboardUtils'

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

	it('should render svg zero-state when there are no errors for the selected day', () => {
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
						personerTotalt: 2,
						nye: 1,
						gjenopprettede: 1,
						pdlFeil: 0,
						andreFeil: 0,
					},
					{
						dato: '2026-06-06',
						personerTotalt: 1,
						nye: 1,
						gjenopprettede: 0,
						pdlFeil: 0,
						andreFeil: 0,
					},
					{
						dato: '2026-06-07',
						personerTotalt: 1,
						nye: 1,
						gjenopprettede: 0,
						pdlFeil: 0,
						andreFeil: 0,
					},
				]}
				previousDaySummary={{
					personerTotalt: 4,
					nye: 3,
					gjenopprettede: 1,
					pdlFeil: 0,
					andreFeil: 0,
					nyeInklGjenopprettede: 4,
					totaltFeil: 0,
				}}
				previousDayChartOptions={{}}
				previousDayErrorBreakdownChartOptions={{}}
			/>,
		)

		expect(
			screen.getByRole('heading', { name: /Statistikk for siste hverdag \+ helg/ }),
		).toBeInTheDocument()
		expect(screen.getByRole('button', { name: 'Siste hverdag + helg' })).toBeInTheDocument()
		expect(
			screen.getByRole('img', { name: 'Ingen feil registrert for valgt dag' }),
		).toBeInTheDocument()
		expect(screen.getByText(/Ingen feil observert, hurra!/)).toBeInTheDocument()
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
						personerTotalt: 4,
						nye: 3,
						gjenopprettede: 1,
						pdlFeil: 0,
						andreFeil: 0,
					},
				]}
				previousDaySummary={{
					personerTotalt: 4,
					nye: 3,
					gjenopprettede: 1,
					pdlFeil: 0,
					andreFeil: 0,
					nyeInklGjenopprettede: 4,
					totaltFeil: 0,
				}}
				previousDayChartOptions={{}}
				previousDayErrorBreakdownChartOptions={{}}
			/>,
		)

		expect(
			screen.getByRole('heading', { name: /Statistikk for siste hverdag/ }),
		).toBeInTheDocument()
		expect(screen.getByRole('button', { name: 'Siste hverdag' })).toBeInTheDocument()
	})
})
