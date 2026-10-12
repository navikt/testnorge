import { type Options, type SeriesColumnOptions } from 'highcharts'
import {
	BAR_COLUMN_PLOT_OPTIONS,
	ROTATED_CATEGORY_LABELS,
	TOOLTIP_OPTIONS,
	withBaseChart,
} from '@/pages/adminPages/Dashboard/dashboardChartBase'
import { createLineTrendChartOptions } from '@/pages/adminPages/Dashboard/dashboardTrendChartOptions'
import {
	type DagFagsystemMatrise,
	type DagPersonMatrise,
	type DetaljPunkt,
	type DetaljType,
	type FagsystemSum,
	type MaanedPunkt,
} from './brukerStatistikkUtils'

const COMPACT_THRESHOLD = 10
const BAR_HEIGHT_PER_CATEGORY = 28
const BAR_HEIGHT_PER_CATEGORY_COMPACT = 20
const BAR_CHART_VERTICAL_PADDING = 80
const BAR_CHART_MIN_HEIGHT = 240

const createHorizontalBarChartOptions = ({
	description,
	seriesName,
	categories,
	data,
}: {
	description: string
	seriesName: string
	categories: string[]
	data: number[]
}): Options => {
	const isCompact = categories.length >= COMPACT_THRESHOLD
	const heightPerCategory = isCompact ? BAR_HEIGHT_PER_CATEGORY_COMPACT : BAR_HEIGHT_PER_CATEGORY
	const chartHeight = Math.max(
		BAR_CHART_MIN_HEIGHT,
		categories.length * heightPerCategory + BAR_CHART_VERTICAL_PADDING,
	)
	return {
		...withBaseChart(description, { type: 'bar', height: chartHeight }),
		xAxis: {
			categories,
			title: { text: undefined },
			labels: { style: { fontSize: '12px' } },
		},
		yAxis: {
			title: { text: seriesName },
			allowDecimals: false,
			min: 0,
		},
		legend: { enabled: false },
		tooltip: { ...TOOLTIP_OPTIONS, pointFormat: '<b>{point.y}</b>' },
		plotOptions: {
			bar: {
				...(isCompact
					? { ...BAR_COLUMN_PLOT_OPTIONS, pointPadding: 0.05, groupPadding: 0.07 }
					: BAR_COLUMN_PLOT_OPTIONS),
				dataLabels: { enabled: true },
			},
		},
		series: [{ type: 'bar', name: seriesName, data }],
	}
}

export const createBrukerTrendChartOptions = (punkter: MaanedPunkt[]): Options =>
	createLineTrendChartOptions({
		description:
			'Linjediagram med månedlig utvikling i nye bestillinger, gjenopprettinger og nye personer.',
		categories: punkter.map((punkt) => punkt.visning),
		legend: {
			enabled: true,
			layout: 'horizontal',
			align: 'left',
			verticalAlign: 'top',
			itemMarginBottom: 4,
		},
		series: [
			{
				type: 'spline',
				name: 'Nye personer',
				data: punkter.map((punkt) => punkt.nyePersoner),
			},
			{
				type: 'spline',
				name: 'Nye bestillinger',
				data: punkter.map((punkt) => punkt.nyeBestillinger),
			},
			{
				type: 'spline',
				name: 'Gjenopprettinger',
				data: punkter.map((punkt) => punkt.gjenopprettinger),
			},
		],
	})

export const createFagsystemFordelingChartOptions = (summer: FagsystemSum[]): Options =>
	createHorizontalBarChartOptions({
		description:
			'Stolpediagram med antall personer bestilt per fagsystem i valgt måned, sortert synkende.',
		seriesName: 'Personer',
		categories: summer.map((sum) => sum.label),
		data: summer.map((sum) => sum.antall),
	})

export const createDagPersonerChartOptions = (matrise: DagPersonMatrise): Options => ({
	...withBaseChart(
		'Stablet søylediagram med nye personer per dag i valgt måned, fordelt på personer opprettet i Dolly og personer importert fra Testnorge.',
		{ type: 'column', height: 320 },
	),
	xAxis: {
		categories: matrise.dager,
		...ROTATED_CATEGORY_LABELS,
	},
	yAxis: {
		title: { text: undefined },
		allowDecimals: false,
		min: 0,
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
			const index = this.points[0]?.index ?? 0
			const header = `<b>Dag ${this.points[0]?.key}</b><br/>`
			const bestillinger = matrise.nyeBestillinger[index] ?? 0
			if (bestillinger === 0) {
				return header + 'Ingen bestillinger'
			}
			const lines = this.points
				.filter((point) => (point.y ?? 0) > 0)
				.map((point) => `${point.series.name}: <b>${point.y}</b>`)
			return header + [`Nye bestillinger: <b>${bestillinger}</b>`, ...lines].join('<br/>')
		},
	},
	plotOptions: {
		column: {
			...BAR_COLUMN_PLOT_OPTIONS,
			stacking: 'normal',
		},
	},
	series: [
		{ type: 'column', name: 'Opprettet i Dolly', data: matrise.dollyPersoner },
		{ type: 'column', name: 'Importert fra Testnorge', data: matrise.testnorgePersoner },
	],
})

export const createFagsystemPerDagChartOptions = (matrise: DagFagsystemMatrise): Options => {
	const series: SeriesColumnOptions[] = matrise.serier.map((serie) => ({
		type: 'column',
		name: serie.label,
		data: serie.data,
	}))

	return {
		...withBaseChart(
			'Stablet søylediagram med antall personer bestilt per dag i valgt måned, fordelt på fagsystem.',
			{ type: 'column', height: 360 },
		),
		xAxis: {
			categories: matrise.dager,
			...ROTATED_CATEGORY_LABELS,
		},
		yAxis: {
			title: { text: undefined },
			allowDecimals: false,
			min: 0,
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
				const header = `<b>Dag ${this.points[0]?.key}</b><br/>`
				const lines = this.points
					.filter((point) => (point.y ?? 0) > 0)
					.map((point) => `${point.series.name}: <b>${point.y}</b>`)
					.join('<br/>')
				return lines ? header + lines : header + 'Ingen bestillinger'
			},
		},
		plotOptions: {
			column: {
				...BAR_COLUMN_PLOT_OPTIONS,
				stacking: 'normal',
			},
		},
		series,
	}
}

const DETALJ_SERIES_NAVN: Record<DetaljType, string> = {
	valg: 'Personer',
	antall: 'Antall oppføringer',
}

export const createFagsystemDetaljerChartOptions = (
	detaljer: DetaljPunkt[],
	fagsystemLabel: string,
	type: DetaljType,
): Options =>
	createHorizontalBarChartOptions({
		description:
			type === 'valg'
				? `Stolpediagram med antall personer per valg for ${fagsystemLabel} i valgt måned.`
				: `Stolpediagram med antall oppføringer per opplysning for ${fagsystemLabel} i valgt måned.`,
		seriesName: DETALJ_SERIES_NAVN[type],
		categories: detaljer.map((detalj) => detalj.label),
		data: detaljer.map((detalj) => detalj.antall),
	})
