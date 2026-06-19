import { type Options } from 'highcharts'
import { BAR_COLUMN_PLOT_OPTIONS, TOOLTIP_OPTIONS, withBaseChart } from './dashboardChartBase'
import { type FeilGruppe } from './dashboardFeilUtils'

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

export const createPreviousDayFeilDonutChartOptions = (feilGrupper: FeilGruppe[]): Options => {
	const errorColors = [
		'var(--ax-danger-700)',
		'var(--ax-danger-600)',
		'var(--ax-danger-500)',
		'var(--ax-warning-700)',
		'var(--ax-warning-600)',
		'var(--ax-warning-500)',
		'var(--ax-meta-orange-700)',
		'var(--ax-meta-orange-600)',
		'var(--ax-meta-red-700)',
		'var(--ax-meta-red-600)',
	]

	const data = feilGrupper.map((gruppe, index) => ({
		name: gruppe.label,
		y: gruppe.rader.length,
		color: errorColors[index % errorColors.length],
	}))

	return {
		...withBaseChart(
			'Sirkeldiagram som viser feilfordeling per fagsystem for siste hverdag.',
			{ type: 'pie', height: 400, margin: [0, 0, 0, 0] },
		),
		plotOptions: {
			pie: {
				dataLabels: {
					enabled: true,
					format: '<b>{point.name}</b>: {point.y}',
					style: { fontSize: '12px' },
				},
				innerSize: '50%',
				depth: 45,
			},
		},
		tooltip: {
			...TOOLTIP_OPTIONS,
			shared: false,
			pointFormat: '<b>{point.name}:</b> {point.y}',
		},
		series: [
			{
				type: 'pie',
				name: 'Feil',
				data,
			},
		],
	}
}
