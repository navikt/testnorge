import { type Options } from 'highcharts'
import {
	BAR_COLUMN_PLOT_OPTIONS,
	ERROR_PRIMARY_COLOR,
	ERROR_SECONDARY_COLOR,
	TOOLTIP_OPTIONS,
	withBaseChart,
} from './dashboardChartBase'

export const createPreviousDayChartOptions = ({
	nye,
	gjenopprettede,
}: {
	nye: number
	gjenopprettede: number
}): Options => ({
	...withBaseChart(
		'Søylediagram som viser forrige dags nøkkeltall fordelt på opprettet og gjenopprettet.',
		{ type: 'column', height: 300, marginBottom: 40, spacing: [16, 16, 10, 16] },
	),
	xAxis: {
		categories: ['Opprettet', 'Gjenopprettet'],
		title: { text: undefined },
		labels: { reserveSpace: true, align: 'center', x: 0, y: 16, style: { fontSize: '12px' } },
	},
	yAxis: {
		title: { text: undefined },
		allowDecimals: false,
		min: 0,
		labels: { y: 0 },
	},
	legend: { enabled: false },
	plotOptions: {
		column: {
			...BAR_COLUMN_PLOT_OPTIONS,
			dataLabels: {
				enabled: true,
				inside: false,
				crop: false,
				overflow: 'allow',
				allowOverlap: true,
				y: -6,
				formatter: function () {
					const pointValue = Number(this.y ?? 0)
					return pointValue > 0 ? `${pointValue}` : ''
				},
			},
		},
	},
	tooltip: {
		...TOOLTIP_OPTIONS,
		shared: false,
		headerFormat: '',
		pointFormat: '{point.key}: <b>{point.y}</b>',
	},
	series: [
		{
			type: 'column',
			name: 'Antall',
			colorByPoint: true,
			data: [
				{ y: nye, color: 'var(--ax-accent-700)' },
				{ y: gjenopprettede, color: 'var(--ax-info-700)' },
			],
		},
	],
})

export const createPreviousDayErrorBreakdownChartOptions = (
	pdlFeil: number,
	andreFeil: number,
): Options => ({
	...withBaseChart(
		'Donutdiagram som viser fordeling av gårsdagens feil mellom PDL-feil og andre feil.',
		{ type: 'pie', height: 300, spacing: [16, 16, 8, 16] },
	),
	legend: { enabled: false },
	tooltip: { ...TOOLTIP_OPTIONS, pointFormat: '<b>{point.y}</b> ({point.percentage:.1f}%)' },
	plotOptions: {
		pie: {
			size: '74%',
			minSize: 220,
			center: ['50%', '50%'],
			innerSize: '55%',
			dataLabels: {
				enabled: true,
				allowOverlap: true,
				formatter: function () {
					const point = this.point as { name?: string; custom?: { actualY?: number }; y?: number }
					const value = point.custom?.actualY ?? point.y ?? 0
					return `${point.name}: ${value}`
				},
			},
		},
	},
	series: [
		{
			type: 'pie',
			name: 'Feil',
			data: [
				{
					name: 'PDL-feil',
					y: pdlFeil === 0 ? 0.0001 : pdlFeil,
					custom: { actualY: pdlFeil },
					color: ERROR_PRIMARY_COLOR,
				},
				{
					name: 'Andre feil',
					y: andreFeil === 0 ? 0.0001 : andreFeil,
					custom: { actualY: andreFeil },
					color: ERROR_SECONDARY_COLOR,
				},
			],
		},
	],
})
