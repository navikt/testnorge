import { type Options, type SeriesColumnOptions } from 'highcharts'
import {
	BAR_COLUMN_PLOT_OPTIONS,
	ROTATED_CATEGORY_LABELS,
	TOOLTIP_OPTIONS,
	withBaseChart,
} from './dashboardChartBase'
import { fagsystemFeilLabel, type FeilDagPunkt, type FeilGruppe } from './dashboardFeilUtils'

const FEIL_CHART_COLORS = [
	'var(--ax-danger-700)',
	'var(--ax-warning-700)',
	'var(--ax-meta-purple-700)',
	'var(--ax-info-700)',
	'var(--ax-accent-700)',
	'var(--ax-neutral-600)',
]

export const createFeilSummertChartOptions = (
	punkter: FeilDagPunkt[],
	fagsystemNokler: string[],
	onDayClick?: (dag: number) => void,
): Options => {
	const series: SeriesColumnOptions[] = fagsystemNokler.map((nokkel) => ({
		type: 'column',
		name: fagsystemFeilLabel(nokkel),
		data: punkter.map((punkt) => punkt.perFagsystem[nokkel] ?? 0),
	}))

	return {
		...withBaseChart(
			'Stablet søylediagram med antall feil per dag i valgt måned, fordelt på fagsystem. Klikk en dag for detaljer.',
			{ type: 'column', height: 360 },
		),
		colors: FEIL_CHART_COLORS,
		xAxis: {
			categories: punkter.map((punkt) => String(punkt.dag).padStart(2, '0')),
			...ROTATED_CATEGORY_LABELS,
		},
		yAxis: {
			title: { text: undefined },
			allowDecimals: false,
			min: 0,
			stackLabels: { enabled: true },
		},
		legend: {
			enabled: true,
			itemStyle: { fontSize: '12px' },
		},
		tooltip: {
			...TOOLTIP_OPTIONS,
			shared: true,
			headerFormat: '<b>{point.key}</b><br/>',
			pointFormat: '{series.name}: <b>{point.y}</b><br/>',
			formatter: function () {
				if (!this.points) return ''
				const header = `<b>${this.points[0]?.key}</b><br/>`
				const lines = this.points
					.filter((point) => point.y > 0)
					.map((point) => `${point.series.name}: <b>${point.y}</b>`)
					.join('<br/>')
				return lines ? header + lines : header + 'Ingen feil'
			},
		},
		plotOptions: {
			column: {
				...BAR_COLUMN_PLOT_OPTIONS,
				stacking: 'normal',
				cursor: onDayClick ? 'pointer' : undefined,
				point: {
					events: {
						click: function () {
							const dag = punkter[this.index]?.dag
							if (onDayClick && typeof dag === 'number') {
								onDayClick(dag)
							}
						},
					},
				},
			},
		},
		series,
	}
}

export const createFeilPerFagsystemChartOptions = (feilGrupper: FeilGruppe[]): Options => ({
	...withBaseChart('Søylediagram med antall feil per fagsystem for valgt periode.', {
		type: 'column',
		height: 360,
	}),
	colors: FEIL_CHART_COLORS,
	xAxis: {
		categories: feilGrupper.map((gruppe) => gruppe.label),
		...ROTATED_CATEGORY_LABELS,
	},
	yAxis: {
		title: { text: undefined },
		allowDecimals: false,
		min: 0,
	},
	legend: { enabled: false },
	tooltip: {
		...TOOLTIP_OPTIONS,
		shared: false,
		headerFormat: '',
		pointFormat: '<b>{point.name}:</b> {point.y}',
	},
	plotOptions: {
		column: {
			...BAR_COLUMN_PLOT_OPTIONS,
			colorByPoint: true,
			dataLabels: {
				enabled: true,
				formatter: function () {
					const pointValue = Number(this.y ?? 0)
					return pointValue > 0 ? `${pointValue}` : ''
				},
			},
		},
	},
	series: [
		{
			type: 'column',
			name: 'Antall feil',
			data: feilGrupper.map((gruppe) => ({ name: gruppe.label, y: gruppe.rader.length })),
		},
	],
})
