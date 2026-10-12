import Highcharts from 'highcharts'
import HighchartsAccessibility from 'highcharts/modules/accessibility'
import { CHART_TEXT_COLOR } from './dashboardChartBase'

const initAccessibilityModule =
	typeof HighchartsAccessibility === 'function'
		? HighchartsAccessibility
		: (HighchartsAccessibility as { default?: (chartInstance: typeof Highcharts) => void }).default
initAccessibilityModule?.(Highcharts)

Highcharts.setOptions({
	palette: { colorScheme: 'light' },
	xAxis: {
		labels: { style: { color: CHART_TEXT_COLOR, fontSize: '12px' } },
		title: { style: { color: CHART_TEXT_COLOR } },
	},
	yAxis: {
		labels: { style: { color: CHART_TEXT_COLOR, fontSize: '12px' } },
		title: { style: { color: CHART_TEXT_COLOR } },
	},
	legend: {
		itemStyle: { color: CHART_TEXT_COLOR },
		itemHoverStyle: { color: CHART_TEXT_COLOR },
	},
	title: { style: { color: CHART_TEXT_COLOR } },
	subtitle: { style: { color: CHART_TEXT_COLOR } },
})
