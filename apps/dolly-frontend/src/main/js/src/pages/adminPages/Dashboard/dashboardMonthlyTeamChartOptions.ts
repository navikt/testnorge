import { type Options } from 'highcharts'
import { getChartBaseOptions, TOOLTIP_OPTIONS } from './dashboardChartBase'
import { TeamDistributionPoint } from './dashboardUtils'

export const createMonthlyTeamDistributionChartOptions = (
	teamDistributionData: TeamDistributionPoint[],
): Options => ({
	...getChartBaseOptions('Stolpediagram med fordeling av unike brukere per team i valgt måned.'),
	chart: {
		...getChartBaseOptions('').chart,
		type: 'bar',
	},
	xAxis: {
		categories: teamDistributionData.map((point) => point.team),
		title: { text: undefined },
		labels: {
			style: {
				fontSize: '12px',
			},
		},
	},
	yAxis: {
		title: { text: 'Unike brukere' },
		allowDecimals: false,
	},
	tooltip: { ...TOOLTIP_OPTIONS, pointFormat: '<b>{point.y}</b>' },
	plotOptions: {
		bar: {
			borderColor: '#FFFFFF',
			borderWidth: 1,
			pointPadding: 0.1,
			groupPadding: 0.12,
		},
	},
	series: [
		{
			type: 'bar',
			name: 'Unike brukere',
			data: teamDistributionData.map((point) => point.unikeBrukere),
		},
	],
})
