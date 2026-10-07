import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'
import type { ReactNode } from 'react'
import { BrukerStatistikk } from '@/pages/minSide/statistikk/BrukerStatistikk'
import {
	useBrukerBestillingerDetaljert,
	useBrukerBestillingerOversikt,
} from '@/utils/hooks/useBrukerStatistikk'

vi.mock('@/utils/hooks/useBrukerStatistikk', () => ({
	useBrukerBestillingerOversikt: vi.fn(),
	useBrukerBestillingerDetaljert: vi.fn(),
}))

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

const mockOversikt = (bestillingerOversikt: unknown[]) =>
	vi.mocked(useBrukerBestillingerOversikt).mockReturnValue({
		bestillingerOversikt,
		loadingBestillingerOversikt: false,
		bestillingerOversiktError: undefined,
	} as ReturnType<typeof useBrukerBestillingerOversikt>)

const mockDetaljert = (bestillingerDetaljert: unknown[]) =>
	vi.mocked(useBrukerBestillingerDetaljert).mockReturnValue({
		bestillingerDetaljert,
		loadingBestillingerDetaljert: false,
		bestillingerDetaljertError: undefined,
	} as ReturnType<typeof useBrukerBestillingerDetaljert>)

describe('BrukerStatistikk', () => {
	it('should show empty state when user has no orders', () => {
		mockOversikt([])
		mockDetaljert([])

		render(<BrukerStatistikk />)

		expect(screen.getByText(/Du har ingen bestillinger enda/)).toBeInTheDocument()
	})

	it('should show KPI values for the latest month and switch drill-down fagsystem', async () => {
		mockOversikt([
			{
				periode: '2026-08',
				antallNyBestillinger: 4,
				antallGjenopprettinger: 2,
				antallNyePersoner: 17,
			},
		])
		mockDetaljert([
			{
				dato: '2026-08-10',
				kriterier: [
					{ fagsystem: 'PdlData', antall: 17, detaljer: { 'Syntetisk-true': 17 } },
					{ fagsystem: 'Aareg', antall: 5, detaljer: { 'Antall arbeidsforhold': 7 } },
					{ fagsystem: 'Krrstub', antall: 3, detaljer: null },
				],
			},
		])

		render(<BrukerStatistikk />)

		expect(useBrukerBestillingerDetaljert).toHaveBeenLastCalledWith(2026, 'AUGUST')
		expect(screen.getByRole('heading', { name: 'Bestillinger i august 2026' })).toBeInTheDocument()
		expect(screen.getByText('17')).toBeInTheDocument()
		expect(screen.getByText('Fagsystem brukt')).toBeInTheDocument()
		expect(screen.getByText('3')).toBeInTheDocument()
		expect(screen.getAllByText('Persondata (PDL)').length).toBeGreaterThan(0)

		expect(screen.getByRole('heading', { name: 'Valg for Persondata (PDL)' })).toBeInTheDocument()
		expect(screen.queryByRole('button', { name: 'Kontakt- og reservasjonsregisteret' })).toBeNull()

		await userEvent.click(screen.getByRole('button', { name: 'Arbeidsforhold (Aareg)' }))

		expect(
			screen.getByRole('heading', { name: 'Oppføringer for Arbeidsforhold (Aareg)' }),
		).toBeInTheDocument()
		expect(screen.queryByRole('heading', { name: 'Valg for Arbeidsforhold (Aareg)' })).toBeNull()
	})
})
