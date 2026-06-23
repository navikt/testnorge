import { type Options, type SeriesLineOptions } from 'highcharts'
import { ROTATED_CATEGORY_LABELS, TOOLTIP_OPTIONS, withBaseChart } from './dashboardChartBase'
import { MonthlyTrendPoint, PersonTrendPoint } from './dashboardUtils'

const createLineTrendChartOptions = ({
	description,
	categories,
	series,
	legend = { enabled: false },
}: {
	description: string
	categories: string[]
	series: SeriesLineOptions[]
	legend?: Options['legend']
}): Options => ({
	...withBaseChart(description, { type: 'line', height: 360, spacing: [16, 16, 4, 16] }),
	xAxis: { categories, ...ROTATED_CATEGORY_LABELS },
	yAxis: { title: { text: undefined }, allowDecimals: false, min: 0 },
	legend,
	tooltip: { ...TOOLTIP_OPTIONS, shared: true },
	plotOptions: { series: { marker: { enabled: true, radius: 3 } } },
	series,
})

export const createPersonTrendChartOptions = (personTrendData: PersonTrendPoint[]): Options =>
	createLineTrendChartOptions({
		description:
			'Linjediagram med daglig utvikling av bestillinger, identer og personer i valgt periode.',
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
				type: 'line',
				name: 'Personer totalt',
				data: personTrendData.map((point) => point.personerTotalt),
				visible: true,
			},
			{ type: 'line', name: 'Nye', data: personTrendData.map((point) => point.nye) },
			{
				type: 'line',
				name: 'Gjenopprettede',
				data: personTrendData.map((point) => point.gjenopprettede),
			},
			{
				type: 'line',
				name: 'NAV-identer',
				data: personTrendData.map((point) => point.navIdenter),
				visible: true,
			},
			{
				type: 'line',
				name: 'Testnorge-identer',
				data: personTrendData.map((point) => point.testnorgeIdenter),
				visible: true,
			},
			{
				type: 'line',
				name: 'Bestillinger',
				data: personTrendData.map((point) => point.bestillinger),
				visible: true,
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
				type: 'line',
				name: 'Unike brukere',
				data: teamTrendData.map((point) => point.totaltUnikeBrukere),
			},
			{
				type: 'line',
				name: options?.secondSeriesName || 'Antall teams',
				data: teamTrendData.map((point) => point.totaltAntallTeams),
			},
		],
	})
