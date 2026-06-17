import { type Options } from 'highcharts'

export const CHART_TEXT_COLOR = '#23262A'

export const CHART_COLORS = [
	'var(--ax-accent-700)',
	'var(--ax-success-700)',
	'var(--ax-warning-700)',
	'var(--ax-danger-700)',
	'var(--ax-meta-purple-700)',
	'var(--ax-info-700)',
]
export const ERROR_PRIMARY_COLOR = 'var(--ax-danger-700)'
export const ERROR_SECONDARY_COLOR = 'var(--ax-warning-700)'

const BASE_CHART_CONFIG: Options['chart'] = {
	backgroundColor: 'transparent',
	spacing: [16, 16, 16, 16],
	style: {
		fontFamily: 'Source Sans 3, sans-serif',
	},
}

export const getChartBaseOptions = (description: string): Options => ({
	chart: { ...BASE_CHART_CONFIG },
	colors: CHART_COLORS,
	credits: {
		enabled: false,
	},
	legend: {
		enabled: true,
		itemStyle: {
			fontSize: '12px',
		},
	},
	title: {
		text: undefined,
	},
	accessibility: {
		enabled: true,
		description,
		keyboardNavigation: {
			enabled: true,
		},
	},
	lang: {
		thousandsSep: ' ',
	},
})

export const withBaseChart = (description: string, chart: Options['chart']): Options => ({
	...getChartBaseOptions(description),
	chart: { ...BASE_CHART_CONFIG, ...chart },
})

export const ROTATED_CATEGORY_LABELS: Options['xAxis'] = {
	title: { text: undefined },
	labels: {
		reserveSpace: true,
		autoRotation: [-45],
		overflow: 'justify',
		style: { fontSize: '12px' },
	},
}

export const BAR_COLUMN_PLOT_OPTIONS = {
	borderColor: '#FFFFFF',
	borderWidth: 1,
	pointPadding: 0.1,
	groupPadding: 0.12,
}

export const TOOLTIP_OPTIONS: Options['tooltip'] = {
	backgroundColor: '#FFFFFF',
	borderColor: '#C6C2BF',
	borderRadius: 8,
	padding: 12,
	shadow: false,
	style: {
		color: CHART_TEXT_COLOR,
		fontSize: '13px',
	},
	outside: true,
}
