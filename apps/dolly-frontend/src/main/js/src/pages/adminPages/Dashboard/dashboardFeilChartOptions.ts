import { type Options, type SeriesColumnOptions } from 'highcharts'
import {
	BAR_COLUMN_PLOT_OPTIONS,
	ROTATED_CATEGORY_LABELS,
	TOOLTIP_OPTIONS,
	withBaseChart,
} from './dashboardChartBase'
import { fagsystemFeilLabel, type FeilDagPunkt } from './dashboardFeilUtils'

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
