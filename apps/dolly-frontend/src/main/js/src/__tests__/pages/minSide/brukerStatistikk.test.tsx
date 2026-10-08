import { render, screen, within } from '@testing-library/react'
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

		render(<BrukerStatistikk eierId="bruker-1" />)

		expect(screen.getByText(/Du har ingen bestillinger enda/)).toBeInTheDocument()
	})

	it('should name the represented team in the empty state and intro', () => {
		mockOversikt([])
		mockDetaljert([])

		const { unmount } = render(<BrukerStatistikk eierId="team-bruker" teamNavn="Team Dolly" />)

		expect(screen.getByText(/Teamet Team Dolly har ingen bestillinger enda/)).toBeInTheDocument()
		unmount()

		mockOversikt([{ periode: '2026-08', antallNyBestillinger: 1, antallNyePersoner: 2 }])
		render(<BrukerStatistikk eierId="team-bruker" teamNavn="Team Dolly" />)

		expect(
			screen.getByText(/bestillingene til teamet Team Dolly, som du representerer nå/),
		).toBeInTheDocument()
		expect(useBrukerBestillingerOversikt).toHaveBeenLastCalledWith('team-bruker')
	})

	it('should show detail values as unavailable when the detail request fails', () => {
		mockOversikt([
			{
				periode: '2026-08',
				antallNyBestillinger: 4,
				antallGjenopprettinger: 2,
				antallNyePersoner: 17,
			},
		])
		vi.mocked(useBrukerBestillingerDetaljert).mockReturnValue({
			bestillingerDetaljert: [],
			loadingBestillingerDetaljert: false,
			bestillingerDetaljertError: new Error('Serverfeil'),
		} as ReturnType<typeof useBrukerBestillingerDetaljert>)

		render(<BrukerStatistikk eierId="bruker-1" />)

		expect(screen.getByText(/Klarte ikke å hente detaljer for valgt måned/)).toBeInTheDocument()
		expect(screen.getAllByText('17')).toHaveLength(2)
		expect(screen.getAllByText('–')).toHaveLength(2)
		expect(screen.queryByText('Ingen')).toBeNull()
		expect(screen.queryByRole('heading', { name: 'Fagsystem i valgt måned' })).toBeNull()
		expect(screen.queryByRole('heading', { name: 'Detaljer per fagsystem' })).toBeNull()
		expect(screen.getByRole('heading', { name: 'Utvikling over tid' })).toBeInTheDocument()
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

		render(<BrukerStatistikk eierId="bruker-1" />)

		expect(useBrukerBestillingerDetaljert).toHaveBeenLastCalledWith('bruker-1', 2026, 'AUGUST')
		expect(useBrukerBestillingerOversikt).toHaveBeenLastCalledWith('bruker-1')
		expect(screen.getByRole('heading', { name: 'August 2026' })).toBeInTheDocument()
		expect(screen.getAllByText('17')).toHaveLength(2)
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

	it('should show yearly totals that follow the year and ignore the month selection', async () => {
		vi.useFakeTimers({ toFake: ['Date'] })
		vi.setSystemTime(new Date(2026, 9, 7))
		mockOversikt([
			{
				periode: '2025-03',
				antallNyBestillinger: 1,
				antallGjenopprettinger: 0,
				antallNyePersoner: 5,
			},
			{
				periode: '2025-11',
				antallNyBestillinger: 6,
				antallGjenopprettinger: 1,
				antallNyePersoner: 40,
			},
			{
				periode: '2026-02',
				antallNyBestillinger: 2,
				antallGjenopprettinger: 3,
				antallNyePersoner: 11,
			},
			{
				periode: '2026-09',
				antallNyBestillinger: 1,
				antallGjenopprettinger: 0,
				antallNyePersoner: 7,
			},
		])
		mockDetaljert([])

		render(<BrukerStatistikk eierId="bruker-1" />)

		const aarPanel = () =>
			within(
				screen.getByRole('heading', { name: /^(Hittil i|Hele) \d{4}$/ })
					.parentElement as HTMLElement,
			)

		expect(screen.getByRole('heading', { name: 'Hittil i 2026' })).toBeInTheDocument()
		expect(aarPanel().getByText('18')).toBeInTheDocument()
		expect(aarPanel().getByText('Februar')).toBeInTheDocument()

		await userEvent.click(screen.getByRole('button', { name: 'Februar' }))

		expect(screen.getByRole('heading', { name: 'Februar 2026' })).toBeInTheDocument()
		expect(aarPanel().getByText('18')).toBeInTheDocument()

		await userEvent.click(screen.getByRole('button', { name: '2025' }))

		expect(screen.getByRole('heading', { name: 'Hele 2025' })).toBeInTheDocument()
		expect(aarPanel().getByText('45')).toBeInTheDocument()
		expect(aarPanel().getByText('7')).toBeInTheDocument()
		expect(aarPanel().getByText('November')).toBeInTheDocument()
		expect(screen.getByRole('heading', { name: 'November 2025' })).toBeInTheDocument()

		vi.useRealTimers()
	})
})
