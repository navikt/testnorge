import { type Options } from 'highcharts'
import {
	ERROR_PRIMARY_COLOR,
	ERROR_SECONDARY_COLOR,
	getChartBaseOptions,
	TOOLTIP_OPTIONS,
} from './dashboardChartBase'

export const createPreviousDayChartOptions = ({
	nye,
	gjenopprettede,
}: {
	nye: number
	gjenopprettede: number
}): Options => ({
	...getChartBaseOptions(
		'Søylediagram som viser forrige dags nøkkeltall fordelt på opprettet og gjenopprettet.',
	),
	chart: {
		...getChartBaseOptions('').chart,
		type: 'column',
		height: 300,
		marginBottom: 40,
		spacing: [16, 16, 10, 16],
	},
	xAxis: {
		categories: ['Opprettet', 'Gjenopprettet'],
		title: { text: undefined },
		labels: {
			reserveSpace: true,
			align: 'center',
			x: 0,
			y: 16,
			style: {
				fontSize: '12px',
			},
		},
	},
	yAxis: {
		title: {
			text: undefined,
		},
		allowDecimals: false,
		min: 0,
		labels: {
			y: 0,
		},
	},
	plotOptions: {
		column: {
			borderColor: '#FFFFFF',
			borderWidth: 1,
			pointPadding: 0.1,
			groupPadding: 0.12,
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
		formatter: function () {
			const value = Number(this.y ?? 0)
			const category =
				typeof this.point?.category === 'string'
					? this.point.category
					: this.x === 0
						? 'Opprettet'
						: this.x === 1
							? 'Gjenopprettet'
							: `${this.x}`
			return `${category}: <b>${value}</b>`
		},
	},
	series: [
		{
			type: 'column',
			name: 'Antall',
			showInLegend: false,
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
	...getChartBaseOptions(
		'Donutdiagram som viser fordeling av gårsdagens feil mellom PDL-feil og andre feil.',
	),
	chart: {
		...getChartBaseOptions('').chart,
		type: 'pie',
		height: 300,
		spacing: [16, 16, 8, 16],
	},
	tooltip: { ...TOOLTIP_OPTIONS, pointFormat: '<b>{point.y}</b> ({point.percentage:.1f}%)' },
	plotOptions: {
		pie: {
			size: '74%',
			minSize: 220,
			center: ['50%', '50%'],
			innerSize: '55%',
			showInLegend: true,
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
	legend: {
		enabled: false,
		labelFormatter: function () {
			const point = this as unknown as { name?: string; custom?: { actualY?: number }; y?: number }
			const value = point.custom?.actualY ?? point.y ?? 0
			return `${point.name}: ${value}`
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
