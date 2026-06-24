import {
	createFeilPerFagsystemChartOptions,
	createFeilSummertChartOptions,
	createIdenterDonutChartOptions,
	createMonthlyTeamDistributionChartOptions,
	createMonthlyTeamTrendChartOptions,
	createNyeGjenopprettedeDonutChartOptions,
	createOpprettetGjenopprettetDonutChartOptions,
	createPersonTrendChartOptions,
} from '@/pages/adminPages/Dashboard/dashboardChartOptions'
import type Highcharts from 'highcharts'

describe('dashboardChartOptions', () => {
	it('should render person trend tooltip outside the plot area', () => {
		const options = createPersonTrendChartOptions([
			{
				dato: '2026-06-01',
				datoVisning: '01.06.2026',
				bestillinger: 12,
				personerTotalt: 10,
				nye: 5,
				gjenopprettede: 2,
				navIdenter: 7,
				testnorgeIdenter: 5,
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
		const series = options.series as Highcharts.SeriesSplineOptions[]
		expect(series).toHaveLength(6)
		expect(series[0].name).toBe('Personer totalt')
		expect(series[1].name).toBe('Nye')
		expect(series[2].name).toBe('Gjenopprettede')
		expect(series[3].name).toBe('NAV-identer')
		expect(series[4].name).toBe('Testnorge-identer')
		expect(series[5].name).toBe('Bestillinger')
		expect(series[0].visible).toBe(true)
		expect(series[0].data).toEqual([10])
		expect(options.chart?.type).toBe('spline')
	})

	it('should create opprettet/gjenopprettet donut with two coloured slices', () => {
		const options = createOpprettetGjenopprettetDonutChartOptions({
			nye: 9,
			gjenopprettede: 3,
		})

		const series = options.series?.[0] as Highcharts.SeriesPieOptions

		expect(options.chart?.type).toBe('pie')
		expect(options.chart?.height).toBe(300)
		expect(options.plotOptions?.pie?.innerSize).toBe('60%')
		expect(options.series).toHaveLength(1)
		expect(series.data).toEqual([
			{ name: 'Opprettet', y: 9, color: 'var(--ax-accent-700)' },
			{ name: 'Gjenopprettet', y: 3, color: 'var(--ax-info-700)' },
		])
		expect(options.tooltip?.outside).toBe(true)
	})

	it('should create nye/gjenopprettede donut with two coloured slices', () => {
		const options = createNyeGjenopprettedeDonutChartOptions({
			nye: 14,
			gjenopprettede: 6,
		})

		const series = options.series?.[0] as Highcharts.SeriesPieOptions

		expect(options.chart?.type).toBe('pie')
		expect(options.chart?.height).toBe(400)
		expect(options.plotOptions?.pie?.innerSize).toBe('60%')
		expect(series.data).toEqual([
			{ name: 'Nye personer', y: 14, color: 'var(--ax-success-700)' },
			{ name: 'Gjenopprettede', y: 6, color: 'var(--ax-accent-700)' },
		])
	})

	it('should create identer donut with NAV and Testnorge slices', () => {
		const options = createIdenterDonutChartOptions({
			navIdenter: 12,
			testnorgeIdenter: 4,
		})

		const series = options.series?.[0] as Highcharts.SeriesPieOptions

		expect(options.chart?.type).toBe('pie')
		expect(options.chart?.height).toBe(400)
		expect(options.plotOptions?.pie?.innerSize).toBe('60%')
		expect(series.data).toEqual([
			{ name: 'NAV-identer', y: 12, color: 'var(--ax-accent-700)' },
			{ name: 'Testnorge-identer', y: 4, color: 'var(--ax-success-700)' },
		])
	})

	it('should create feil-per-fagsystem column chart with one coloured bar per fagsystem', () => {
		const options = createFeilPerFagsystemChartOptions([
			{
				feilNokkel: 'pdlPersonFeil',
				label: 'PDL Person',
				rader: [
					{ ident: '1', bestillingId: 1, sistOppdatert: '', master: 'PDL', verdi: 'feil' },
					{ ident: '2', bestillingId: 2, sistOppdatert: '', master: 'PDL', verdi: 'feil' },
				],
			},
			{
				feilNokkel: 'andreFeil',
				label: 'Andre feil',
				rader: [{ ident: '3', bestillingId: 3, sistOppdatert: '', master: 'PDL', verdi: 'feil' }],
			},
		])

		const series = options.series?.[0] as Highcharts.SeriesColumnOptions

		expect(options.chart?.type).toBe('column')
		expect(options.legend?.enabled).toBe(false)
		expect(options.plotOptions?.column?.colorByPoint).toBe(true)
		expect(
			options.xAxis && !Array.isArray(options.xAxis) ? options.xAxis.categories : undefined,
		).toEqual(['PDL Person', 'Andre feil'])
		expect(series.data).toEqual([
			{ name: 'PDL Person', y: 2 },
			{ name: 'Andre feil', y: 1 },
		])
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
		expect(options.chart?.type).toBe('spline')
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
