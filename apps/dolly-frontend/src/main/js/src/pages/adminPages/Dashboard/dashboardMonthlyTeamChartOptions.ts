import { type Options } from 'highcharts'
import { BAR_COLUMN_PLOT_OPTIONS, TOOLTIP_OPTIONS, withBaseChart } from './dashboardChartBase'
import { TeamDistributionPoint } from './dashboardUtils'

export const createMonthlyTeamDistributionChartOptions = (
	teamDistributionData: TeamDistributionPoint[],
): Options => ({
	...withBaseChart('Stolpediagram med fordeling av unike brukere per team i valgt måned.', {
		type: 'bar',
	}),
	xAxis: {
		categories: teamDistributionData.map((point) => point.team),
		title: { text: undefined },
		labels: { style: { fontSize: '12px' } },
	},
	yAxis: {
		title: { text: 'Unike brukere' },
		allowDecimals: false,
	},
	legend: { enabled: false },
	tooltip: { ...TOOLTIP_OPTIONS, pointFormat: '<b>{point.y}</b>' },
	plotOptions: { bar: BAR_COLUMN_PLOT_OPTIONS },
	series: [
		{
			type: 'bar',
			name: 'Unike brukere',
			data: teamDistributionData.map((point) => point.unikeBrukere),
		},
	],
})
