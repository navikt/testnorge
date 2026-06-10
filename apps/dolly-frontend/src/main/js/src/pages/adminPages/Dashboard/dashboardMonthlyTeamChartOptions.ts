import { type Options } from 'highcharts'
import { BAR_COLUMN_PLOT_OPTIONS, TOOLTIP_OPTIONS, withBaseChart } from './dashboardChartBase'
import { TeamDistributionPoint } from './dashboardUtils'

const BAR_HEIGHT_PER_CATEGORY = 28
const BAR_CHART_VERTICAL_PADDING = 80
const BAR_CHART_MIN_HEIGHT = 320

export const createMonthlyTeamDistributionChartOptions = (
	teamDistributionData: TeamDistributionPoint[],
	seriesName = 'Unike brukere',
): Options => {
	const chartHeight = Math.max(
		BAR_CHART_MIN_HEIGHT,
		teamDistributionData.length * BAR_HEIGHT_PER_CATEGORY + BAR_CHART_VERTICAL_PADDING,
	)
	return {
		...withBaseChart('Stolpediagram med fordeling per team i valgt måned.', {
			type: 'bar',
			height: chartHeight,
		}),
		xAxis: {
			categories: teamDistributionData.map((point) => point.team),
			title: { text: undefined },
			labels: { style: { fontSize: '12px' } },
		},
		yAxis: {
			title: { text: seriesName },
			allowDecimals: false,
		},
		legend: { enabled: false },
		tooltip: { ...TOOLTIP_OPTIONS, pointFormat: '<b>{point.y}</b>' },
		plotOptions: { bar: BAR_COLUMN_PLOT_OPTIONS },
		series: [
			{
				type: 'bar',
				name: seriesName,
				data: teamDistributionData.map((point) => point.unikeBrukere),
			},
		],
	}
}
