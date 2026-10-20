import { type Options, type SeriesSplineOptions } from 'highcharts'
import { ROTATED_CATEGORY_LABELS, TOOLTIP_OPTIONS, withBaseChart } from './dashboardChartBase'
import { MonthlyPersonPoint, MonthlyTrendPoint, PersonTrendPoint } from './dashboardUtils'

export const createLineTrendChartOptions = ({
	description,
	categories,
	series,
	legend = { enabled: false },
}: {
	description: string
	categories: string[]
	series: SeriesSplineOptions[]
	legend?: Options['legend']
}): Options => ({
	...withBaseChart(description, { type: 'spline', height: 360, spacing: [16, 16, 4, 16] }),
	xAxis: { categories, ...ROTATED_CATEGORY_LABELS },
	yAxis: { title: { text: undefined }, allowDecimals: false, min: 0 },
	legend,
	tooltip: { ...TOOLTIP_OPTIONS, shared: true },
	plotOptions: { spline: { marker: { enabled: true, radius: 3 } } },
	series,
})

export const createPersonTrendChartOptions = (personTrendData: PersonTrendPoint[]): Options =>
	createLineTrendChartOptions({
		description:
			'Linjediagram med daglig utvikling av nye personer og bestillinger i valgt periode.',
		categories: personTrendData.map((point) => point.datoVisning),
		legend: {
			enabled: true,
			layout: 'horizontal',
			align: 'left',
			verticalAlign: 'top',
			itemMarginBottom: 4,
		},
		series: [
			{
				type: 'spline',
				name: 'Nye personer',
				data: personTrendData.map((point) => point.nyePersoner),
			},
			{
				type: 'spline',
				name: 'Opprettet i Dolly',
				data: personTrendData.map((point) => point.dollyPersoner),
			},
			{
				type: 'spline',
				name: 'Importert fra Testnorge',
				data: personTrendData.map((point) => point.testnorgePersoner),
			},
			{
				type: 'spline',
				name: 'Nye bestillinger',
				data: personTrendData.map((point) => point.nyeBestillinger),
			},
			{
				type: 'spline',
				name: 'Gjenopprettede bestillinger',
				data: personTrendData.map((point) => point.gjenopprettedeBestillinger),
			},
		],
	})

export const createMonthlyTeamTrendChartOptions = (
	teamTrendData: MonthlyTrendPoint[],
	options?: {
		description?: string
		secondSeriesName?: string
	},
): Options =>
	createLineTrendChartOptions({
		description:
			options?.description ||
			'Linjediagram med månedlig utvikling i unike brukere og antall aktive teams.',
		categories: teamTrendData.map((point) => point.intervalVisning),
		series: [
			{
				type: 'spline',
				name: 'Unike brukere',
				data: teamTrendData.map((point) => point.totaltUnikeBrukere),
			},
			{
				type: 'spline',
				name: options?.secondSeriesName || 'Antall teams',
				data: teamTrendData.map((point) => point.totaltAntallTeams),
			},
		],
	})

export const createMonthlyPersonTrendChartOptions = (points: MonthlyPersonPoint[]): Options =>
	createLineTrendChartOptions({
		description:
			'Linjediagram med antall personer bestilt per måned, fordelt på nye og gjenopprettede bestillinger.',
		categories: points.map((point) => point.intervalVisning),
		legend: {
			enabled: true,
			layout: 'horizontal',
			align: 'left',
			verticalAlign: 'top',
			itemMarginBottom: 4,
		},
		series: [
			{
				type: 'spline',
				name: 'Personer totalt',
				data: points.map((point) => point.totalt),
			},
			{
				type: 'spline',
				name: 'Fra nye bestillinger',
				data: points.map((point) => point.nye),
			},
			{
				type: 'spline',
				name: 'Fra gjenopprettede bestillinger',
				data: points.map((point) => point.gjenopprettede),
			},
		],
	})
