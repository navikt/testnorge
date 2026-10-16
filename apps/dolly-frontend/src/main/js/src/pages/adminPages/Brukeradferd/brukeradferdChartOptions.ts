import { type Options, type SeriesColumnOptions } from 'highcharts'
import {
	BAR_COLUMN_PLOT_OPTIONS,
	ROTATED_CATEGORY_LABELS,
	TOOLTIP_OPTIONS,
	withBaseChart,
} from '@/pages/adminPages/Dashboard/dashboardChartBase'
import {
	type AdferdGranularitet,
	type AdferdTidView,
	type FagsystemTotal,
	formaterAndel,
	type PersonTidPunkt,
} from './brukeradferdUtils'
import { fagsystemLabel } from '@/pages/statistikk/fagsystemLabels'

const ADFERD_FARGETONER = [
	'accent',
	'success',
	'warning',
	'meta-purple',
	'info',
	'brand-magenta',
	'meta-lime',
	'danger',
]

export const ADFERD_CHART_COLORS = [700, 400, 900].flatMap((nyanse) =>
	ADFERD_FARGETONER.map((tone) => `var(--ax-${tone}-${nyanse})`),
)

const OVER_TID_HOEYDE = 380
const LEGEND_ELEMENTER_PER_RAD = 5
const LEGEND_RAD_HOEYDE = 24

const BAR_HOEYDE = 32
const MIN_FORDELING_HOEYDE = 240

export const createFagsystemFordelingChartOptions = (
	totaler: FagsystemTotal[],
	onFagsystemClick?: (fagsystem: string) => void,
	harDetaljer: (fagsystem: string) => boolean = () => true,
): Options => ({
	...withBaseChart(
		'Liggende søylediagram med antall fagsystemvalg per fagsystem i valgt periode, sortert fra mest til minst brukt. Klikk et fagsystem med detaljer for å vise dem.',
		{
			type: 'bar',
			height: Math.max(MIN_FORDELING_HOEYDE, totaler.length * BAR_HOEYDE + 80),
		},
	),
	colors: ['var(--ax-accent-700)'],
	xAxis: {
		categories: totaler.map((total) => total.label),
		title: { text: undefined },
		labels: { style: { fontSize: '12px' } },
	},
	yAxis: {
		title: { text: undefined },
		allowDecimals: false,
		min: 0,
	},
	legend: { enabled: false },
	tooltip: {
		...TOOLTIP_OPTIONS,
		formatter: function () {
			const total = totaler[this.index]
			if (!total) return ''
			const klikkHint =
				onFagsystemClick && harDetaljer(total.fagsystem) ? '<br/>Klikk for detaljer' : ''
			return `<b>${total.label}</b><br/>Fagsystemvalg: <b>${total.antall}</b><br/>Andel: <b>${formaterAndel(total.andel)}</b>${klikkHint}`
		},
	},
	plotOptions: {
		bar: {
			...BAR_COLUMN_PLOT_OPTIONS,
			cursor: onFagsystemClick ? 'pointer' : undefined,
			dataLabels: { enabled: true },
			point: {
				events: {
					click: function () {
						const fagsystem = totaler[this.index]?.fagsystem
						if (onFagsystemClick && fagsystem && harDetaljer(fagsystem)) {
							onFagsystemClick(fagsystem)
						}
					},
				},
			},
		},
	},
	series: [
		{
			type: 'bar',
			name: 'Fagsystemvalg',
			data: totaler.map((total) => ({ name: total.label, y: total.antall })),
		},
	],
})

export const createAdferdOverTidChartOptions = (
	view: AdferdTidView,
	granularitet: AdferdGranularitet,
	onPunktClick?: (punktNoekkel: string) => void,
): Options => {
	const series: SeriesColumnOptions[] = view.fagsystemNoekler.map((noekkel, indeks) => ({
		type: 'column',
		name: fagsystemLabel(noekkel),
		color: ADFERD_CHART_COLORS[indeks % ADFERD_CHART_COLORS.length],
		data: view.punkter.map((punkt) => punkt.perFagsystem[noekkel] ?? 0),
	}))
	const legendRader = Math.ceil(series.length / LEGEND_ELEMENTER_PER_RAD)
	const hoeyde = OVER_TID_HOEYDE + Math.max(0, legendRader - 2) * LEGEND_RAD_HOEYDE
	const periodeTekst = granularitet === 'DAG' ? 'dag i valgt måned' : 'måned i valgt periode'

	return {
		...withBaseChart(
			`Stablet søylediagram med antall fagsystemvalg per ${periodeTekst}, fordelt på fagsystem. Klikk en søyle for detaljer.`,
			{ type: 'column', height: hoeyde },
		),
		xAxis: {
			categories: view.punkter.map((punkt) => punkt.kategori),
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
			formatter: function () {
				if (!this.points) return ''
				const punkt = view.punkter[this.points[0]?.index ?? 0]
				const header = `<b>${punkt?.visning ?? this.points[0]?.key}</b><br/>`
				const lines = this.points
					.filter((point) => (point.y ?? 0) > 0)
					.sort((a, b) => (b.y ?? 0) - (a.y ?? 0))
					.map((point) => `${point.series.name}: <b>${point.y}</b>`)
					.join('<br/>')
				return lines ? header + lines : header + 'Ingen fagsystemvalg'
			},
		},
		plotOptions: {
			column: {
				...BAR_COLUMN_PLOT_OPTIONS,
				stacking: 'normal',
				cursor: onPunktClick ? 'pointer' : undefined,
				point: {
					events: {
						click: function () {
							const noekkel = view.punkter[this.index]?.noekkel
							if (onPunktClick && noekkel) {
								onPunktClick(noekkel)
							}
						},
					},
				},
			},
		},
		series,
	}
}

export const createNyePersonerChartOptions = (
	punkter: PersonTidPunkt[],
	granularitet: AdferdGranularitet,
	onPunktClick?: (punktNoekkel: string) => void,
): Options => {
	const periodeTekst = granularitet === 'DAG' ? 'dag i valgt måned' : 'måned i valgt periode'

	return {
		...withBaseChart(
			`Stablet søylediagram med nye personer per ${periodeTekst}, fordelt på personer opprettet i Dolly og personer importert fra Testnorge. Klikk en søyle for detaljer.`,
			{ type: 'column', height: 340 },
		),
		xAxis: {
			categories: punkter.map((punkt) => punkt.kategori),
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
			formatter: function () {
				if (!this.points) return ''
				const punkt = punkter[this.points[0]?.index ?? 0]
				if (!punkt) return ''
				return [
					`<b>${punkt.visning}</b>`,
					`Opprettet i Dolly: <b>${punkt.dollyPersoner}</b>`,
					`Importert fra Testnorge: <b>${punkt.testnorgePersoner}</b>`,
					`Nye personer: <b>${punkt.nyePersoner}</b>`,
					`Nye bestillinger: <b>${punkt.nyeBestillinger}</b>`,
				].join('<br/>')
			},
		},
		plotOptions: {
			column: {
				...BAR_COLUMN_PLOT_OPTIONS,
				stacking: 'normal',
				cursor: onPunktClick ? 'pointer' : undefined,
				point: {
					events: {
						click: function () {
							const noekkel = punkter[this.index]?.noekkel
							if (onPunktClick && noekkel) {
								onPunktClick(noekkel)
							}
						},
					},
				},
			},
		},
		series: [
			{
				type: 'column',
				name: 'Opprettet i Dolly',
				color: 'var(--ax-accent-700)',
				data: punkter.map((punkt) => punkt.dollyPersoner),
			},
			{
				type: 'column',
				name: 'Importert fra Testnorge',
				color: 'var(--ax-success-700)',
				data: punkter.map((punkt) => punkt.testnorgePersoner),
			},
		],
	}
}
