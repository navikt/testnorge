import { render, screen, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'
import type { ReactNode } from 'react'
import { BrukerStatistikk } from '@/pages/statistikk/BrukerStatistikk'
import {
	useBrukerBestillingerDetaljert,
	useBrukerBestillingerDetaljertForMaaneder,
	useBrukerBestillingerOversikt,
} from '@/utils/hooks/useBrukerStatistikk'

vi.mock('@/utils/hooks/useBrukerStatistikk', () => ({
	useBrukerBestillingerOversikt: vi.fn(),
	useBrukerBestillingerDetaljert: vi.fn(),
	useBrukerBestillingerDetaljertForMaaneder: vi.fn(),
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

const mockDetaljertForAar = (bestillingerDetaljert: unknown[]) =>
	vi.mocked(useBrukerBestillingerDetaljertForMaaneder).mockReturnValue({
		bestillingerDetaljert,
		loadingBestillingerDetaljert: false,
		bestillingerDetaljertError: undefined,
	} as ReturnType<typeof useBrukerBestillingerDetaljertForMaaneder>)

describe('BrukerStatistikk', () => {
	beforeEach(() => {
		mockDetaljertForAar([])
	})

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

		mockOversikt([{ periode: '2026-08', antallNyeBestillinger: 1, antallNyePersoner: 2 }])
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
				antallNyeBestillinger: 4,
				antallGjenopprettedeBestillinger: 2,
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
		expect(screen.getAllByText('17')).toHaveLength(1)
		expect(screen.getAllByText('–')).toHaveLength(2)
		expect(screen.queryByText('Ingen')).toBeNull()
		expect(screen.queryByRole('heading', { name: 'Fagsystem i aug. 2026' })).toBeNull()
		expect(screen.queryByRole('heading', { name: 'Detaljer per fagsystem i aug. 2026' })).toBeNull()
		expect(screen.getByRole('heading', { name: 'Utvikling over tid' })).toBeInTheDocument()
	})

	it('should show KPI values for the latest month and switch drill-down fagsystem', async () => {
		mockOversikt([
			{
				periode: '2026-08',
				antallNyeBestillinger: 4,
				antallGjenopprettedeBestillinger: 2,
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
		const periodeRegion = within(screen.getByRole('region', { name: 'Bestillinger per år' }))
		expect(
			periodeRegion.getByRole('heading', { name: 'Fagsystem i aug. 2026' }),
		).toBeInTheDocument()
		expect(
			periodeRegion.getByRole('heading', { name: 'Detaljer per fagsystem i aug. 2026' }),
		).toBeInTheDocument()
		expect(periodeRegion.queryByRole('heading', { name: 'Utvikling over tid' })).toBeNull()
		expect(
			screen.getByRole('radio', { name: 'Kun valgt måned' }).getAttribute('aria-checked'),
		).toBe('true')
		expect(screen.getAllByText('17')).toHaveLength(1)
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

	it('should split new persons by origin and ignore unspecified fagsystem', () => {
		mockOversikt([
			{
				periode: '2026-10',
				antallNyeBestillinger: 10,
				antallGjenopprettedeBestillinger: 0,
				antallNyePersoner: 18,
				andelOpprettedeDollyPersoner: 15,
				andelImporterteTestnorgePersoner: 3,
			},
		])
		mockDetaljert([
			{
				dato: '2026-10-02',
				antallNyeBestillinger: 3,
				antallNyePersoner: 3,
				andelOpprettedeDollyPersoner: 3,
				andelImporterteTestnorgePersoner: 0,
				kriterier: [
					{ fagsystem: 'Uspesifisert', antall: 9, detaljer: {} },
					{ fagsystem: 'Skattekort', antall: 1, detaljer: {} },
					{ fagsystem: 'PdlData', antall: 3, detaljer: { 'Syntetisk-true': 3 } },
				],
			},
			{
				dato: '2026-10-06',
				antallNyeBestillinger: 7,
				antallNyePersoner: 15,
				andelOpprettedeDollyPersoner: 12,
				andelImporterteTestnorgePersoner: 3,
				kriterier: [{ fagsystem: 'Skattekort', antall: 3, detaljer: {} }],
			},
		])

		render(<BrukerStatistikk eierId="bruker-1" />)

		const periodeRegion = within(screen.getByRole('region', { name: 'Bestillinger per år' }))
		expect(periodeRegion.getByText('Opprettet i Dolly').nextSibling?.textContent).toBe('15')
		expect(periodeRegion.getByText('Importert fra Testnorge').nextSibling?.textContent).toBe('3')
		expect(
			periodeRegion.getByRole('heading', { name: 'Nye personer per dag i okt. 2026' }),
		).toBeInTheDocument()
		expect(
			periodeRegion.getByText(
				'Nye personer per dag fordelt på opprettet i Dolly og importert fra Testnorge',
			),
		).toBeInTheDocument()
		expect(periodeRegion.getByText('Skattekort')).toBeInTheDocument()
		expect(periodeRegion.queryByRole('button', { name: 'Skattekort' })).toBeNull()
		expect(periodeRegion.getByRole('button', { name: 'Persondata (PDL)' })).toBeInTheDocument()
	})

	it('should let the year view drive every panel and switch back on month selection', async () => {
		vi.useFakeTimers({ toFake: ['Date'] })
		vi.setSystemTime(new Date(2026, 9, 7))
		mockOversikt([
			{
				periode: '2025-03',
				antallNyeBestillinger: 1,
				antallGjenopprettedeBestillinger: 0,
				antallNyePersoner: 5,
			},
			{
				periode: '2025-11',
				antallNyeBestillinger: 6,
				antallGjenopprettedeBestillinger: 1,
				antallNyePersoner: 40,
			},
			{
				periode: '2026-02',
				antallNyeBestillinger: 2,
				antallGjenopprettedeBestillinger: 3,
				antallNyePersoner: 11,
			},
			{
				periode: '2026-09',
				antallNyeBestillinger: 1,
				antallGjenopprettedeBestillinger: 0,
				antallNyePersoner: 7,
			},
		])
		mockDetaljert([])

		render(<BrukerStatistikk eierId="bruker-1" />)

		const erValgt = (navn: string) =>
			screen.getByRole('radio', { name: navn }).getAttribute('aria-checked') === 'true'

		expect(erValgt('Kun valgt måned')).toBe(true)
		expect(screen.queryByText('Aktive måneder')).toBeNull()

		await userEvent.click(screen.getByRole('radio', { name: 'Hittil i 2026' }))

		expect(erValgt('Hittil i 2026')).toBe(true)
		expect(screen.getByText('18')).toBeInTheDocument()
		expect(screen.getByText('Februar')).toBeInTheDocument()

		expect(
			screen.getByRole('heading', { name: 'Fagsystem per måned hittil i 2026' }),
		).toBeInTheDocument()
		expect(useBrukerBestillingerDetaljertForMaaneder).toHaveBeenLastCalledWith('bruker-1', [
			{ year: 2026, month: 'FEBRUARY' },
			{ year: 2026, month: 'SEPTEMBER' },
		])

		await userEvent.click(screen.getByRole('button', { name: 'feb., med i tidsrommet' }))

		expect(erValgt('Kun valgt måned')).toBe(true)
		expect(
			screen.getByRole('heading', { name: 'Fagsystem per dag i feb. 2026' }),
		).toBeInTheDocument()
		expect(useBrukerBestillingerDetaljertForMaaneder).toHaveBeenLastCalledWith('bruker-1', [])

		await userEvent.click(screen.getByRole('radio', { name: 'Hittil i 2026' }))

		await userEvent.click(screen.getByRole('button', { name: '2025' }))

		expect(erValgt('Hele 2025')).toBe(true)
		expect(screen.getByText('45')).toBeInTheDocument()
		expect(screen.getByText('7')).toBeInTheDocument()
		expect(screen.getByText('November')).toBeInTheDocument()
		expect(erValgt('Kun valgt måned')).toBe(false)
		expect(screen.getByRole('heading', { name: 'Fagsystem i 2025' })).toBeInTheDocument()
		expect(
			screen.getByRole('heading', { name: 'Detaljer per fagsystem i 2025' }),
		).toBeInTheDocument()
		expect(useBrukerBestillingerDetaljertForMaaneder).toHaveBeenLastCalledWith('bruker-1', [
			{ year: 2025, month: 'MARCH' },
			{ year: 2025, month: 'NOVEMBER' },
		])

		await userEvent.click(screen.getByRole('radio', { name: 'Kun valgt måned' }))

		expect(erValgt('Kun valgt måned')).toBe(true)
		expect(screen.getByText('40')).toBeInTheDocument()
		expect(screen.queryByText('Aktive måneder')).toBeNull()

		vi.useRealTimers()
	})
})
