import { type Options, type SeriesLineOptions } from 'highcharts'
import {
	ERROR_PRIMARY_COLOR,
	ERROR_SECONDARY_COLOR,
	ROTATED_CATEGORY_LABELS,
	TOOLTIP_OPTIONS,
	withBaseChart,
} from './dashboardChartBase'
import { MonthlyTrendPoint, PersonTrendPoint } from './dashboardUtils'

const createLineTrendChartOptions = ({
	description,
	categories,
	series,
	legend = { enabled: false },
}: {
	description: string
	categories: string[]
	series: SeriesLineOptions[]
	legend?: Options['legend']
}): Options => ({
	...withBaseChart(description, { type: 'line', height: 360, spacing: [16, 16, 4, 16] }),
	xAxis: { categories, ...ROTATED_CATEGORY_LABELS },
	yAxis: { title: { text: undefined }, allowDecimals: false, min: 0 },
	legend,
	tooltip: { ...TOOLTIP_OPTIONS, shared: true },
	plotOptions: { series: { marker: { enabled: true, radius: 3 } } },
	series,
})

type PersonTrendVisibilityOptions = {
	personerTotaltVisible?: boolean
	feilTotaltVisible?: boolean
	onPersonerTotaltVisibilityChange?: (visible: boolean) => void
	onFeilTotaltVisibilityChange?: (visible: boolean) => void
}

export const createPersonTrendChartOptions = (
	personTrendData: PersonTrendPoint[],
	visibilityOptions?: PersonTrendVisibilityOptions,
): Options =>
	createLineTrendChartOptions({
		description:
			'Linjediagram med daglig utvikling av nye, gjenopprettede, PDL-feil og andre feil i valgt periode.',
		categories: personTrendData.map((point) => point.datoVisning),
		legend: {
			enabled: true,
			layout: 'horizontal',
			align: 'left',
			verticalAlign: 'top',
			itemMarginBottom: 4,
		},
		series: [
			{ type: 'line', name: 'Nye', data: personTrendData.map((point) => point.nye) },
			{
				type: 'line',
				name: 'Gjenopprettede',
				data: personTrendData.map((point) => point.gjenopprettede),
			},
			{
				type: 'line',
				name: 'PDL-feil',
				data: personTrendData.map((point) => point.pdlFeil),
				color: ERROR_PRIMARY_COLOR,
			},
			{
				type: 'line',
				name: 'Andre feil',
				data: personTrendData.map((point) => point.andreFeil),
				color: ERROR_SECONDARY_COLOR,
			},
			{
				type: 'line',
				name: 'Personer totalt',
				data: personTrendData.map((point) => point.personerTotalt),
				visible: visibilityOptions?.personerTotaltVisible ?? false,
				events: {
					show: () => visibilityOptions?.onPersonerTotaltVisibilityChange?.(true),
					hide: () => visibilityOptions?.onPersonerTotaltVisibilityChange?.(false),
				},
			},
			{
				type: 'line',
				name: 'Feil totalt',
				data: personTrendData.map((point) => point.pdlFeil + point.andreFeil),
				visible: visibilityOptions?.feilTotaltVisible ?? false,
				events: {
					show: () => visibilityOptions?.onFeilTotaltVisibilityChange?.(true),
					hide: () => visibilityOptions?.onFeilTotaltVisibilityChange?.(false),
				},
			},
		],
	})

export const createMonthlyTeamTrendChartOptions = (
	teamTrendData: MonthlyTrendPoint[],
	options?: {
		description?: string
		secondSeriesName?: string
	},
): Options =>
	createLineTrendChartOptions({
		description:
			options?.description ||
			'Linjediagram med månedlig utvikling i unike brukere og antall aktive teams.',
		categories: teamTrendData.map((point) => point.intervalVisning),
		series: [
			{
				type: 'line',
				name: 'Unike brukere',
				data: teamTrendData.map((point) => point.totaltUnikeBrukere),
			},
			{
				type: 'line',
				name: options?.secondSeriesName || 'Antall teams',
				data: teamTrendData.map((point) => point.totaltAntallTeams),
			},
		],
	})
