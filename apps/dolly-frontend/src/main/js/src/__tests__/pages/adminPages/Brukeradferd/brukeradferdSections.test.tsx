import { render, screen, within } from '@testing-library/react'
import { vi } from 'vitest'
import type { ReactNode } from 'react'
import {
	AdferdOverTidSection,
	BrukeradferdPanel,
	FagsystemDetaljerSection,
	FagsystemFordelingSection,
	NyePersonerSection,
	ValgtPunktSection,
} from '@/pages/adminPages/Brukeradferd/brukeradferdSections'
import {
	type BrukeradferdNoekkeltall,
	type PersonNoekkeltall,
	type PersonTidPunkt,
} from '@/pages/adminPages/Brukeradferd/brukeradferdUtils'
import { type PeriodeVisning } from '@/pages/statistikk/brukerStatistikkUtils'

vi.mock('@/pages/adminPages/Dashboard/dashboardSharedComponents', () => ({
	DashboardChartPanel: ({ ariaLabel }: { ariaLabel: string }) => <div>{ariaLabel}</div>,
	DashboardKpiCard: ({ label, value }: { label: string; value: ReactNode }) => (
		<div>
			<div>{label}</div>
			<div>{value}</div>
		</div>
	),
	DashboardSelectButtons: ({
		label,
		options,
		selected,
		onSelect,
	}: {
		label: string
		options: { value: string; label: string }[]
		selected: string | null
		onSelect: (value: string) => void
	}) => (
		<div role="group" aria-label={label}>
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

vi.mock('@/components/ui/loading/DollySpinner', () => ({
	default: ({ label }: { label: string }) => <div>{label}</div>,
}))

const TOTALER = [
	{ fagsystem: 'PdlData', label: 'PDL', antall: 15, andel: 0.75 },
	{ fagsystem: 'Aareg', label: 'Aareg', antall: 5, andel: 0.25 },
]

const PERSONER: PersonNoekkeltall = {
	nyeBestillinger: 6,
	nyePersoner: 22,
	dollyPersoner: 18,
	testnorgePersoner: 4,
}

const TOMME_PERSONER: PersonNoekkeltall = {
	nyeBestillinger: 0,
	nyePersoner: 0,
	dollyPersoner: 0,
	testnorgePersoner: 0,
}

const NOEKKELTALL: BrukeradferdNoekkeltall = {
	totaltFagsystemvalg: 20,
	fagsystemerIBruk: 2,
	mestBrukt: TOTALER[0],
	dagerMedBestillinger: 3,
}

const TOMME_NOEKKELTALL: BrukeradferdNoekkeltall = {
	totaltFagsystemvalg: 0,
	fagsystemerIBruk: 0,
	mestBrukt: null,
	dagerMedBestillinger: 0,
}

const PERSON_PUNKTER: PersonTidPunkt[] = [
	{ noekkel: '2026-09-02', kategori: '02.09', visning: '02.09.2026', ...PERSONER },
]

const periodeProps = {
	yearOptions: ['2025', '2026'],
	selectedYear: '2026',
	onYearChange: vi.fn(),
	monthOptions: [{ value: '2026-09', label: 'sep.' }],
	selectedInterval: '2026-09',
	onIntervalChange: vi.fn(),
	visning: 'maaned' as PeriodeVisning,
	onVisningChange: vi.fn(),
	noekkeltall: NOEKKELTALL,
	personNoekkeltall: PERSONER,
}

const detaljProps = {
	fagsystemOptions: [
		{ value: 'PdlData', label: 'PDL' },
		{ value: 'Aareg', label: 'Aareg' },
	],
	valgtFagsystem: 'PdlData',
	onFagsystemChange: vi.fn(),
	valgtFagsystemLabel: 'PDL',
	valgChartOptions: {},
	antallChartOptions: {},
	periodeTittel: 'i sep. 2026',
}

describe('brukeradferd sections', () => {
	it('should render period selectors in one panel and propagate view changes', () => {
		vi.useFakeTimers({ toFake: ['Date'] })
		vi.setSystemTime(new Date(2026, 9, 13))
		const onVisningChange = vi.fn()
		render(
			<BrukeradferdPanel {...periodeProps} onVisningChange={onVisningChange}>
				<div>Innhold</div>
			</BrukeradferdPanel>,
		)

		expect(screen.getByRole('heading', { level: 2, name: 'Bestillinger per periode' })).toBeTruthy()
		expect(screen.getByRole('group', { name: 'År' })).toBeTruthy()
		expect(screen.getByRole('group', { name: 'Måned' })).toBeTruthy()
		expect(screen.getByText('Innhold')).toBeTruthy()

		const tidsrom = screen.getByRole('radiogroup', { name: 'Vis statistikk for' })
		const radioer = within(tidsrom).getAllByRole('radio')
		expect(radioer.map((radio) => radio.textContent)).toEqual(['Kun valgt måned', 'Hittil i 2026'])
		expect(radioer[0].getAttribute('aria-checked')).toBe('true')
		within(tidsrom).getByRole('radio', { name: 'Hittil i 2026' }).click()
		expect(onVisningChange).toHaveBeenCalledWith('aar')
		vi.useRealTimers()
	})

	it('should render empty state and hide content when no periods are available', () => {
		render(
			<BrukeradferdPanel {...periodeProps} yearOptions={[]} monthOptions={[]}>
				<div>Innhold</div>
			</BrukeradferdPanel>,
		)

		expect(screen.getByText('Ingen perioder tilgjengelig.')).toBeTruthy()
		expect(screen.queryByText('Innhold')).toBeNull()
		expect(screen.queryByText('Nye bestillinger')).toBeNull()
	})

	it('should render key figures in the panel header in Min statistikk order', () => {
		render(
			<BrukeradferdPanel {...periodeProps}>
				<div>Innhold</div>
			</BrukeradferdPanel>,
		)

		expect(screen.queryByRole('heading', { name: 'Nøkkeltall' })).toBeNull()
		const labels = [
			'Nye bestillinger',
			'Fagsystemvalg totalt',
			'Fagsystemer i bruk',
			'Mest brukte fagsystem',
			'Nye personer',
			'Opprettet i Dolly',
			'Importert fra Testnorge',
			'Dager med bestillinger',
		].map((label) => screen.getByText(label))
		labels.slice(1).forEach((label, index) => {
			expect(
				labels[index].compareDocumentPosition(label) & Node.DOCUMENT_POSITION_FOLLOWING,
			).toBeTruthy()
		})
		expect(screen.getByText('Persondata (PDL)')).toBeTruthy()
		expect(screen.getByText('18')).toBeTruthy()
	})

	it('should show abbreviated most used fagsystem with full name as title', () => {
		render(
			<BrukeradferdPanel
				{...periodeProps}
				noekkeltall={{
					totaltFagsystemvalg: 2850,
					fagsystemerIBruk: 12,
					mestBrukt: {
						fagsystem: 'Krrstub',
						label: 'Kontakt- og reservasjonsregisteret',
						antall: 565,
						andel: 0.198,
					},
					dagerMedBestillinger: 20,
				}}
			>
				<div>Innhold</div>
			</BrukeradferdPanel>,
		)

		expect(screen.getByTitle('Kontakt- og reservasjonsregisteret').textContent).toBe('KRR')
	})

	it('should show zero values and no most used fagsystem without data', () => {
		render(
			<BrukeradferdPanel
				{...periodeProps}
				noekkeltall={TOMME_NOEKKELTALL}
				personNoekkeltall={TOMME_PERSONER}
			>
				<div>Innhold</div>
			</BrukeradferdPanel>,
		)

		expect(screen.getByText('Ingen')).toBeTruthy()
		expect(screen.getAllByText('0')).toHaveLength(7)
	})

	it('should hide key figure values while loading', () => {
		render(
			<BrukeradferdPanel {...periodeProps} isLoadingNoekkeltall>
				<div>Innhold</div>
			</BrukeradferdPanel>,
		)

		expect(screen.getAllByText('–')).toHaveLength(8)
		expect(screen.queryByText('18')).toBeNull()
	})

	it('should render new persons chart with period title and table alternative', () => {
		render(
			<NyePersonerSection
				punkter={PERSON_PUNKTER}
				granularitet="DAG"
				chartOptions={{}}
				periodeTittel="i sep. 2026"
			/>,
		)

		expect(
			screen.getByRole('heading', { level: 3, name: 'Nye personer per dag i sep. 2026' }),
		).toBeTruthy()
		expect(
			screen.getByText(
				'Nye personer per dag fordelt på opprettet i Dolly og importert fra Testnorge',
			),
		).toBeTruthy()
		expect(screen.getByRole('button', { name: 'Vis som tabell' })).toBeTruthy()
	})

	it('should render empty state for new persons', () => {
		render(
			<NyePersonerSection punkter={[]} granularitet="MAANED" chartOptions={{}} periodeTittel="" />,
		)

		expect(screen.getByRole('heading', { level: 3, name: 'Nye personer per måned' })).toBeTruthy()
		expect(screen.getByText('Ingen nye personer i valgt periode.')).toBeTruthy()
	})

	it('should render distribution chart and table alternative', () => {
		render(
			<FagsystemFordelingSection
				fagsystemTotaler={TOTALER}
				chartOptions={{}}
				periodeTittel="i sep. 2026"
			/>,
		)

		expect(screen.getByRole('heading', { level: 3, name: 'Fagsystem i sep. 2026' })).toBeTruthy()
		expect(screen.getByText('Antall fagsystemvalg per fagsystem')).toBeTruthy()
		expect(screen.getByRole('button', { name: 'Vis som tabell' })).toBeTruthy()
	})

	it('should show spinner instead of chart while loading', () => {
		render(
			<FagsystemFordelingSection
				fagsystemTotaler={TOTALER}
				chartOptions={{}}
				periodeTittel="i sep. 2026"
				isLoading
			/>,
		)

		expect(screen.queryByText('Antall fagsystemvalg per fagsystem')).toBeNull()
		expect(screen.getByText('Laster statistikk...')).toBeTruthy()
	})

	it('should render selected day panel with persons and close it', () => {
		const onLukk = vi.fn()
		render(
			<ValgtPunktSection
				granularitet="DAG"
				valgtPunkt="2026-09-02"
				valgtPunktVisning="02.09.2026"
				valgtPunktTotaler={TOTALER}
				valgtPunktPersoner={PERSONER}
				onLukk={onLukk}
			/>,
		)

		expect(screen.getByRole('heading', { level: 3, name: 'Valgt dag: 02.09.2026' })).toBeTruthy()
		expect(screen.getByText('Nye personer')).toBeTruthy()
		expect(screen.getByText('Nye bestillinger')).toBeTruthy()
		expect(screen.getByRole('table', { name: 'Fagsystemvalg 02.09.2026' })).toBeTruthy()
		screen.getByRole('button', { name: 'Lukk' }).click()
		expect(onLukk).toHaveBeenCalled()
	})

	it('should render over time chart with day and month wording', () => {
		const { rerender } = render(
			<AdferdOverTidSection
				harTidsdata
				granularitet="DAG"
				chartOptions={{}}
				periodeTittel="i sep. 2026"
			/>,
		)

		expect(screen.getByRole('heading', { name: 'Fagsystem per dag i sep. 2026' })).toBeTruthy()

		rerender(
			<AdferdOverTidSection
				harTidsdata
				granularitet="MAANED"
				chartOptions={{}}
				periodeTittel="hittil i 2026"
			/>,
		)

		expect(screen.getByRole('heading', { name: 'Fagsystem per måned hittil i 2026' })).toBeTruthy()
	})

	it('should render fagsystem buttons and detail charts like Min statistikk', () => {
		const onFagsystemChange = vi.fn()
		render(<FagsystemDetaljerSection {...detaljProps} onFagsystemChange={onFagsystemChange} />)

		expect(
			screen.getByRole('heading', { level: 3, name: 'Detaljer per fagsystem i sep. 2026' }),
		).toBeTruthy()
		expect(screen.getByRole('heading', { level: 4, name: 'Valg for PDL' })).toBeTruthy()
		expect(screen.getByRole('heading', { level: 4, name: 'Oppføringer for PDL' })).toBeTruthy()
		expect(screen.getByRole('button', { name: 'PDL' }).getAttribute('aria-pressed')).toBe('true')

		screen.getByRole('button', { name: 'Aareg' }).click()

		expect(onFagsystemChange).toHaveBeenCalledWith('Aareg')
	})

	it('should hide detail charts without data', () => {
		render(
			<FagsystemDetaljerSection
				{...detaljProps}
				valgChartOptions={null}
				antallChartOptions={{}}
				valgtFagsystem="Aareg"
				valgtFagsystemLabel="Aareg"
			/>,
		)

		expect(screen.queryByRole('heading', { level: 4, name: 'Valg for Aareg' })).toBeNull()
		expect(screen.getByRole('heading', { level: 4, name: 'Oppføringer for Aareg' })).toBeTruthy()
	})

	it('should render empty state when no fagsystem has details', () => {
		render(
			<FagsystemDetaljerSection
				{...detaljProps}
				fagsystemOptions={[]}
				valgtFagsystem={null}
				valgChartOptions={null}
				antallChartOptions={null}
			/>,
		)

		expect(screen.getByText('Ingen fagsystemer med detaljer i valgt periode.')).toBeTruthy()
	})
})
