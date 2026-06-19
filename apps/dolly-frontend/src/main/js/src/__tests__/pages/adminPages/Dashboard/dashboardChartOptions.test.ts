import {
	createFeilSummertChartOptions,
	createMonthlyTeamDistributionChartOptions,
	createMonthlyTeamTrendChartOptions,
	createPersonTrendChartOptions,
	createPreviousDayChartOptions,
} from '@/pages/adminPages/Dashboard/dashboardChartOptions'
import type Highcharts from 'highcharts'

describe('dashboardChartOptions', () => {
	it('should render person trend tooltip outside the plot area', () => {
		const options = createPersonTrendChartOptions([
			{
				dato: '2026-06-01',
				datoVisning: '01.06.2026',
				personerTotalt: 10,
				nye: 5,
				gjenopprettede: 2,
			},
		])

		expect(options.tooltip?.shared).toBe(true)
		expect(options.tooltip?.outside).toBe(true)
		expect(options.chart?.height).toBe(360)
		expect(options.legend?.enabled).toBe(true)
		expect(options.legend?.verticalAlign).toBe('top')
		expect(
			options.xAxis && !Array.isArray(options.xAxis)
				? options.xAxis.labels?.autoRotation
				: undefined,
		).toEqual([-45])
		expect(options.yAxis && !Array.isArray(options.yAxis) ? options.yAxis.min : undefined).toBe(0)
		const series = options.series as Highcharts.SeriesLineOptions[]
		expect(series).toHaveLength(3)
		expect(series[0].name).toBe('Nye')
		expect(series[1].name).toBe('Gjenopprettede')
		expect(series[2].name).toBe('Personer totalt')
		expect(series[2].visible).toBe(false)
		expect(series[2].data).toEqual([10])
	})

	it('should honor persisted visibility for total series in person trend chart', () => {
		const onPersonerTotaltVisibilityChange = vi.fn()
		const options = createPersonTrendChartOptions(
			[
				{
					dato: '2026-06-01',
					datoVisning: '01.06.2026',
					personerTotalt: 10,
					nye: 5,
					gjenopprettede: 2,
				},
			],
			{
				personerTotaltVisible: true,
				onPersonerTotaltVisibilityChange,
			},
		)
		const series = options.series as Highcharts.SeriesLineOptions[]

		expect(series[2].visible).toBe(true)
		series[2].events?.hide?.call({} as never)
		series[2].events?.show?.call({} as never)
		expect(onPersonerTotaltVisibilityChange).toHaveBeenNthCalledWith(1, false)
		expect(onPersonerTotaltVisibilityChange).toHaveBeenNthCalledWith(2, true)
	})

	it('should create yesterday comparison chart with two key metrics', () => {
		const options = createPreviousDayChartOptions({
			nye: 9,
			gjenopprettede: 3,
		})

		const series = options.series?.[0] as Highcharts.SeriesColumnOptions

		expect(options.series).toHaveLength(1)
		expect(
			options.xAxis && !Array.isArray(options.xAxis) ? options.xAxis.categories : undefined,
		).toEqual(['Opprettet', 'Gjenopprettet'])
		expect(series.name).toBe('Antall')
		expect(series.data).toEqual([
			{ y: 9, color: 'var(--ax-accent-700)' },
			{ y: 3, color: 'var(--ax-info-700)' },
		])
		expect(options.tooltip?.shared).toBe(false)
		expect(options.tooltip?.headerFormat).toBe('')
		expect(options.tooltip?.pointFormat).toBe('{point.key}: <b>{point.y}</b>')
		expect(options.chart?.height).toBe(300)
		expect(options.chart?.marginBottom).toBe(40)
		expect(
			options.xAxis && !Array.isArray(options.xAxis)
				? options.xAxis.labels?.reserveSpace
				: undefined,
		).toBe(true)
		expect(
			options.xAxis && !Array.isArray(options.xAxis) ? options.xAxis.labels?.align : undefined,
		).toBe('center')
		expect(
			options.xAxis && !Array.isArray(options.xAxis) ? options.xAxis.labels?.x : undefined,
		).toBe(0)
		expect(
			options.xAxis && !Array.isArray(options.xAxis) ? options.xAxis.labels?.y : undefined,
		).toBe(16)
		expect(
			options.yAxis && !Array.isArray(options.yAxis) ? options.yAxis.labels?.y : undefined,
		).toBe(0)
	})

	it('should create stacked feil-per-day chart with one series per fagsystem and day-click wiring', () => {
		const onDayClick = vi.fn()
		const options = createFeilSummertChartOptions(
			[
				{
					dag: 3,
					dato: '2026-06-03',
					datoVisning: '03.06.2026',
					total: 4,
					perFagsystem: { pdlPersonFeil: 3, andreFeil: 1 },
				},
				{
					dag: 7,
					dato: '2026-06-07',
					datoVisning: '07.06.2026',
					total: 2,
					perFagsystem: { pdlPersonFeil: 2, andreFeil: 0 },
				},
			],
			['pdlPersonFeil', 'andreFeil'],
			onDayClick,
		)

		const series = options.series as Highcharts.SeriesColumnOptions[]
		expect(series).toHaveLength(2)
		expect(series[0].data).toEqual([3, 2])
		expect(series[1].data).toEqual([1, 0])
		expect(
			options.xAxis && !Array.isArray(options.xAxis) ? options.xAxis.categories : undefined,
		).toEqual(['03', '07'])
		expect(
			options.plotOptions?.column?.stacking === 'normal' ||
				options.plotOptions?.series?.stacking === 'normal',
		).toBe(true)

		const clickHandler = options.plotOptions?.column?.point?.events?.click
		expect(typeof clickHandler).toBe('function')
		clickHandler?.call({ index: 1 } as never, {} as never)
		expect(onDayClick).toHaveBeenCalledWith(7)
	})

	it('should create monthly teams trend chart with two series', () => {
		const options = createMonthlyTeamTrendChartOptions([
			{
				interval: '2026-05',
				intervalVisning: 'mai 2026',
				totaltUnikeBrukere: 8,
				totaltAntallTeams: 3,
			},
		])

		expect(options.tooltip?.outside).toBe(true)
		expect(options.chart?.height).toBe(360)
		expect(options.legend?.enabled).toBe(false)
		expect(
			options.xAxis && !Array.isArray(options.xAxis)
				? options.xAxis.labels?.autoRotation
				: undefined,
		).toEqual([-45])
		expect(options.series).toHaveLength(2)
		expect((options.series?.[0] as Highcharts.SeriesLineOptions).name).toBe('Unike brukere')
		expect((options.series?.[1] as Highcharts.SeriesLineOptions).name).toBe('Antall teams')
	})

	it('should create monthly team distribution chart with bar data', () => {
		const options = createMonthlyTeamDistributionChartOptions([
			{ team: 'Team A', unikeBrukere: 4 },
			{ team: 'Team B', unikeBrukere: 2 },
		])
		const series = options.series?.[0] as Highcharts.SeriesBarOptions

		expect(options.chart?.type).toBe('bar')
		expect(options.tooltip?.outside).toBe(true)
		expect(options.legend?.enabled).toBe(false)
		expect(
			options.yAxis && !Array.isArray(options.yAxis) ? options.yAxis.title?.text : undefined,
		).toBe('Unike brukere')
		expect(series.data).toEqual([4, 2])
	})
})
