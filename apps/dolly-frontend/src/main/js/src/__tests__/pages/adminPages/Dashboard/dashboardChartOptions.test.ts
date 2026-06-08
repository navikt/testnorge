import {
	createGenericTrendChartOptions,
	createMonthlyTeamDistributionChartOptions,
	createMonthlyTeamTrendChartOptions,
	createPersonTrendChartOptions,
	createPreviousDayChartOptions,
	createPreviousDayErrorBreakdownChartOptions,
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
				pdlFeil: 1,
				andreFeil: 0,
			},
		])

		expect(options.tooltip?.shared).toBe(true)
		expect(options.tooltip?.outside).toBe(true)
		expect(options.chart?.height).toBe(360)
		expect(options.chart?.marginBottom).toBe(56)
		expect(options.legend?.enabled).toBe(true)
		expect(options.legend?.verticalAlign).toBe('top')
		expect(
			options.xAxis && !Array.isArray(options.xAxis)
				? options.xAxis.labels?.autoRotation
				: undefined,
		).toEqual([-45, -70])
		expect(options.yAxis && !Array.isArray(options.yAxis) ? options.yAxis.min : undefined).toBe(0)
		const series = options.series as Highcharts.SeriesLineOptions[]
		expect(series[2].color).toBe('var(--ax-danger-700)')
		expect(series[3].color).toBe('var(--ax-warning-700)')
		expect(series[4].name).toBe('Personer totalt')
		expect(series[4].visible).toBe(false)
		expect(series[4].data).toEqual([10])
		expect(series[5].name).toBe('Feil totalt')
		expect(series[5].visible).toBe(false)
		expect(series[5].data).toEqual([1])
	})

	it('should honor persisted visibility for total series in person trend chart', () => {
		const onPersonerTotaltVisibilityChange = vi.fn()
		const onFeilTotaltVisibilityChange = vi.fn()
		const options = createPersonTrendChartOptions(
			[
				{
					dato: '2026-06-01',
					datoVisning: '01.06.2026',
					personerTotalt: 10,
					nye: 5,
					gjenopprettede: 2,
					pdlFeil: 1,
					andreFeil: 0,
				},
			],
			{
				personerTotaltVisible: true,
				feilTotaltVisible: true,
				onPersonerTotaltVisibilityChange,
				onFeilTotaltVisibilityChange,
			},
		)
		const series = options.series as Highcharts.SeriesLineOptions[]

		expect(series[4].visible).toBe(true)
		expect(series[5].visible).toBe(true)
		series[4].events?.hide?.call({} as never)
		series[4].events?.show?.call({} as never)
		series[5].events?.hide?.call({} as never)
		series[5].events?.show?.call({} as never)
		expect(onPersonerTotaltVisibilityChange).toHaveBeenNthCalledWith(1, false)
		expect(onPersonerTotaltVisibilityChange).toHaveBeenNthCalledWith(2, true)
		expect(onFeilTotaltVisibilityChange).toHaveBeenNthCalledWith(1, false)
		expect(onFeilTotaltVisibilityChange).toHaveBeenNthCalledWith(2, true)
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
		const tooltipFormatter = options.tooltip?.formatter
		const tooltipOutput = tooltipFormatter?.call({
			x: 'Opprettet',
			y: 9,
		} as never)
		expect(tooltipOutput).toBe('Opprettet: <b>9</b>')
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

	it('should create yesterday error breakdown as pie data', () => {
		const options = createPreviousDayErrorBreakdownChartOptions(5, 2)
		const pieSeries = options.series?.[0] as Highcharts.SeriesPieOptions
		const data = pieSeries.data as Array<{
			name: string
			y: number
			color?: string
			custom?: { actualY?: number }
		}>

		expect(options.chart?.type).toBe('pie')
		expect(options.chart?.height).toBe(300)
		expect(
			options.plotOptions?.pie && !Array.isArray(options.plotOptions.pie)
				? options.plotOptions.pie.size
				: undefined,
		).toBe('74%')
		expect(data).toEqual([
			{ name: 'PDL-feil', y: 5, color: 'var(--ax-danger-700)', custom: { actualY: 5 } },
			{ name: 'Andre feil', y: 2, color: 'var(--ax-warning-700)', custom: { actualY: 2 } },
		])
	})

	it('should preserve zero-value error labels in donut data', () => {
		const options = createPreviousDayErrorBreakdownChartOptions(1, 0)
		const pieSeries = options.series?.[0] as Highcharts.SeriesPieOptions
		const data = pieSeries.data as Array<{ y: number; custom?: { actualY?: number } }>

		expect(data[0].custom?.actualY).toBe(1)
		expect(data[1].custom?.actualY).toBe(0)
		expect(data[1].y).toBeGreaterThan(0)
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
		expect(options.chart?.marginBottom).toBe(56)
		expect(options.legend?.enabled).toBe(false)
		expect(
			options.xAxis && !Array.isArray(options.xAxis)
				? options.xAxis.labels?.autoRotation
				: undefined,
		).toEqual([-45, -70])
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
		expect(series.showInLegend).toBe(false)
		expect(series.data).toEqual([4, 2])
	})

	it('should create generic trend chart with legend and line series', () => {
		const options = createGenericTrendChartOptions({
			description: 'Generic trend',
			labels: ['01.06.2026', '02.06.2026'],
			series: [
				{ key: 'nye', name: 'Nye', data: [1, 2], total: 3 },
				{ key: 'feil', name: 'Feil', data: [0, 1], total: 1 },
			],
		})

		expect(options.chart?.type).toBe('line')
		expect(options.legend?.enabled).toBe(true)
		expect(options.series).toHaveLength(2)
		expect((options.series?.[0] as Highcharts.SeriesLineOptions).name).toBe('Nye')
	})
})
