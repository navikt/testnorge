import { type Options } from 'highcharts'

export const CHART_COLORS = [
	'var(--ax-accent-700)',
	'var(--ax-success-700)',
	'var(--ax-warning-700)',
	'var(--ax-danger-700)',
	'var(--ax-meta-purple-700)',
	'var(--ax-info-700)',
]
export const ERROR_PRIMARY_COLOR = 'var(--ax-danger-700)'
export const ERROR_SECONDARY_COLOR = 'var(--ax-danger-500)'

export const getChartBaseOptions = (description: string): Options => ({
	chart: {
		backgroundColor: 'transparent',
		spacing: [16, 16, 16, 16],
		style: {
			fontFamily: 'Source Sans 3, sans-serif',
		},
	},
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

export const TOOLTIP_OPTIONS: Options['tooltip'] = {
	backgroundColor: '#FFFFFF',
	borderColor: '#C6C2BF',
	borderRadius: 8,
	padding: 12,
	shadow: false,
	style: {
		color: '#23262A',
		fontSize: '13px',
	},
	outside: true,
}
