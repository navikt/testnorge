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
		marginBottom: 72,
	},
	xAxis: {
		categories: ['Opprettet', 'Gjenopprettet'],
		title: { text: undefined },
		labels: {
			reserveSpace: true,
			style: {
				fontSize: '12px',
			},
		},
	},
	yAxis: {
		title: {
			text: 'Antall',
		},
		allowDecimals: false,
		min: 0,
		labels: {
			y: -4,
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
				formatter: function () {
					const pointValue = Number(this.y ?? 0)
					return pointValue > 0 ? `${pointValue}` : ''
				},
			},
		},
	},
	tooltip: {
		...TOOLTIP_OPTIONS,
		shared: true,
		formatter: function () {
			const hoveredPoints = (this.points ?? []).filter((point) => Number(point.y ?? 0) > 0)
			const pointRows = hoveredPoints.map(
				(point) =>
					`<span style="color:${point.color}">\u25cf</span> ${point.series.name}: <b>${point.y}</b>`,
			)
			return pointRows.join('<br/>')
		},
	},
	series: [
		{
			type: 'column',
			name: 'Opprettet',
			color: 'var(--ax-accent-700)',
			data: [nye, 0],
		},
		{
			type: 'column',
			name: 'Gjenopprettet',
			color: 'var(--ax-info-700)',
			data: [0, gjenopprettede],
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
		enabled: true,
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
