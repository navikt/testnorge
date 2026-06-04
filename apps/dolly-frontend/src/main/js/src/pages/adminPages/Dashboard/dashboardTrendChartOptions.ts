import { type Options } from 'highcharts'
import {
	ERROR_PRIMARY_COLOR,
	ERROR_SECONDARY_COLOR,
	getChartBaseOptions,
	TOOLTIP_OPTIONS,
} from './dashboardChartBase'
import { MonthlyTrendPoint, PersonTrendPoint } from './dashboardUtils'

export const createPersonTrendChartOptions = (personTrendData: PersonTrendPoint[]): Options => ({
	...getChartBaseOptions(
		'Linjediagram med daglig utvikling av nye, gjenopprettede, PDL-feil og andre feil i valgt periode.',
	),
	xAxis: {
		categories: personTrendData.map((point) => point.datoVisning),
		title: { text: 'Dato' },
		labels: {
			style: {
				fontSize: '12px',
			},
		},
	},
	yAxis: {
		title: { text: 'Antall' },
		allowDecimals: false,
	},
	tooltip: { ...TOOLTIP_OPTIONS, shared: true },
	plotOptions: {
		series: {
			marker: {
				enabled: true,
				radius: 3,
			},
		},
	},
	series: [
		{ type: 'line', name: 'Nye', data: personTrendData.map((point) => point.nye) },
		{
			type: 'line',
			name: 'Gjenopprettede',
			data: personTrendData.map((point) => point.gjenopprettede),
		},
		{
			type: 'line',
			name: 'PDL-feil',
			data: personTrendData.map((point) => point.pdlFeil),
			color: ERROR_PRIMARY_COLOR,
		},
		{
			type: 'line',
			name: 'Andre feil',
			data: personTrendData.map((point) => point.andreFeil),
			color: ERROR_SECONDARY_COLOR,
		},
	],
})

export const createMonthlyTeamTrendChartOptions = (
	teamTrendData: MonthlyTrendPoint[],
): Options => ({
	...getChartBaseOptions(
		'Linjediagram med månedlig utvikling i unike brukere og antall aktive teams.',
	),
	chart: {
		...getChartBaseOptions('').chart,
		type: 'line',
	},
	xAxis: {
		categories: teamTrendData.map((point) => point.intervalVisning),
		title: { text: 'Måned' },
		labels: {
			rotation: -30,
			style: {
				fontSize: '12px',
			},
		},
	},
	yAxis: {
		title: { text: 'Antall' },
		allowDecimals: false,
	},
	tooltip: { ...TOOLTIP_OPTIONS, shared: true },
	series: [
		{
			type: 'line',
			name: 'Unike brukere',
			data: teamTrendData.map((point) => point.totaltUnikeBrukere),
		},
		{
			type: 'line',
			name: 'Antall teams',
			data: teamTrendData.map((point) => point.totaltAntallTeams),
		},
	],
})
