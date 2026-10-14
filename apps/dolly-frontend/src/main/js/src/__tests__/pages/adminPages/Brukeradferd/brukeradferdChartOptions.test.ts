import { vi } from 'vitest'
import type Highcharts from 'highcharts'
import {
	createAdferdOverTidChartOptions,
	createFagsystemFordelingChartOptions,
	createNyePersonerChartOptions,
} from '@/pages/adminPages/Brukeradferd/brukeradferdChartOptions'
import {
	type AdferdTidView,
	type FagsystemTotal,
	type PersonTidPunkt,
} from '@/pages/adminPages/Brukeradferd/brukeradferdUtils'

const PERSON_PUNKTER: PersonTidPunkt[] = [
	{
		noekkel: '2026-09-01',
		kategori: '01.09',
		visning: '01.09.2026',
		nyeBestillinger: 2,
		nyePersoner: 6,
		dollyPersoner: 6,
		testnorgePersoner: 0,
	},
	{
		noekkel: '2026-09-02',
		kategori: '02.09',
		visning: '02.09.2026',
		nyeBestillinger: 4,
		nyePersoner: 16,
		dollyPersoner: 12,
		testnorgePersoner: 4,
	},
]

const TOTALER: FagsystemTotal[] = [
	{ fagsystem: 'PdlData', label: 'PDL', antall: 15, andel: 0.75 },
	{ fagsystem: 'Aareg', label: 'Aareg', antall: 5, andel: 0.25 },
]

const TID_VIEW: AdferdTidView = {
	punkter: [
		{
			noekkel: '2026-09-01',
			kategori: '01.09',
			visning: '01.09.2026',
			total: 6,
			perFagsystem: { PdlData: 5, Aareg: 1 },
		},
		{
			noekkel: '2026-09-02',
			kategori: '02.09',
			visning: '02.09.2026',
			total: 14,
			perFagsystem: { PdlData: 10, Aareg: 4 },
		},
	],
	fagsystemNoekler: ['PdlData', 'Aareg'],
}

describe('brukeradferdChartOptions', () => {
	it('should create a horizontal bar chart sorted as given with accessible description', () => {
		const options = createFagsystemFordelingChartOptions(TOTALER)

		expect(options.chart?.type).toBe('bar')
		expect((options.xAxis as Highcharts.XAxisOptions).categories).toEqual(['PDL', 'Aareg'])
		expect(options.accessibility?.description).toContain('Liggende søylediagram')
		const serie = options.series?.[0] as Highcharts.SeriesBarOptions
		expect(serie.data).toEqual([
			{ name: 'PDL', y: 15 },
			{ name: 'Aareg', y: 5 },
		])
	})

	it('should scale bar chart height with number of fagsystemer', () => {
		const mange = Array.from({ length: 20 }, (_, index) => ({
			fagsystem: `F${index}`,
			label: `F${index}`,
			antall: 1,
			andel: 0.05,
		}))

		expect(createFagsystemFordelingChartOptions(TOTALER).chart?.height).toBe(240)
		expect(createFagsystemFordelingChartOptions(mange).chart?.height).toBe(20 * 32 + 80)
	})

	it('should call fagsystem click handler with fagsystem key', () => {
		const onClick = vi.fn()
		const options = createFagsystemFordelingChartOptions(TOTALER, onClick)
		const clickHandler = options.plotOptions?.bar?.point?.events?.click as unknown as (this: {
			index: number
		}) => void

		clickHandler.call({ index: 1 })

		expect(onClick).toHaveBeenCalledWith('Aareg')
		expect(options.plotOptions?.bar?.cursor).toBe('pointer')
	})

	it('should ignore clicks on fagsystemer without details', () => {
		const onClick = vi.fn()
		const options = createFagsystemFordelingChartOptions(
			TOTALER,
			onClick,
			(fagsystem) => fagsystem === 'PdlData',
		)
		const clickHandler = options.plotOptions?.bar?.point?.events?.click as unknown as (this: {
			index: number
		}) => void

		clickHandler.call({ index: 1 })
		expect(onClick).not.toHaveBeenCalled()

		clickHandler.call({ index: 0 })
		expect(onClick).toHaveBeenCalledWith('PdlData')
	})

	it('should create one stacked column series per fagsystem with distinct colors', () => {
		const options = createAdferdOverTidChartOptions(TID_VIEW, 'DAG')
		const series = options.series as Highcharts.SeriesColumnOptions[]

		expect(options.plotOptions?.column?.stacking).toBe('normal')
		expect((options.xAxis as Highcharts.XAxisOptions).categories).toEqual(['01.09', '02.09'])
		expect(series.map((serie) => serie.name)).toEqual([
			'Persondata (PDL)',
			'Arbeidsforhold (Aareg)',
		])
		expect(series[0].data).toEqual([5, 10])
		expect(series[0].color).not.toBe(series[1].color)
		expect(options.accessibility?.description).toContain('dag i valgt måned')
	})

	it('should give many fagsystemer distinct colors and room for the legend', () => {
		const fagsystemer = Array.from({ length: 20 }, (_, indeks) => `Fagsystem${indeks}`)
		const options = createAdferdOverTidChartOptions(
			{ punkter: TID_VIEW.punkter, fagsystemNoekler: fagsystemer },
			'DAG',
		)
		const series = options.series as Highcharts.SeriesColumnOptions[]

		expect(series).toHaveLength(20)
		expect(new Set(series.map((serie) => serie.color)).size).toBe(20)
		expect(options.chart?.height).toBeGreaterThan(
			createAdferdOverTidChartOptions(TID_VIEW, 'DAG').chart?.height as number,
		)
	})

	it('should describe month granularity and call click handler with punkt key', () => {
		const onClick = vi.fn()
		const options = createAdferdOverTidChartOptions(TID_VIEW, 'MAANED', onClick)
		const clickHandler = options.plotOptions?.column?.point?.events?.click as unknown as (this: {
			index: number
		}) => void

		clickHandler.call({ index: 0 })

		expect(onClick).toHaveBeenCalledWith('2026-09-01')
		expect(options.accessibility?.description).toContain('måned i valgt periode')
	})

	it('should create stacked Dolly and Testnorge series for new persons', () => {
		const onClick = vi.fn()
		const options = createNyePersonerChartOptions(PERSON_PUNKTER, 'DAG', onClick)
		const series = options.series as Highcharts.SeriesColumnOptions[]
		const clickHandler = options.plotOptions?.column?.point?.events?.click as unknown as (this: {
			index: number
		}) => void

		clickHandler.call({ index: 1 })

		expect(options.plotOptions?.column?.stacking).toBe('normal')
		expect((options.xAxis as Highcharts.XAxisOptions).categories).toEqual(['01.09', '02.09'])
		expect(series.map((serie) => serie.name)).toEqual([
			'Opprettet i Dolly',
			'Importert fra Testnorge',
		])
		expect(series[0].data).toEqual([6, 12])
		expect(series[1].data).toEqual([0, 4])
		expect(onClick).toHaveBeenCalledWith('2026-09-02')
		expect(options.accessibility?.description).toContain('nye personer per dag i valgt måned')
	})
})
