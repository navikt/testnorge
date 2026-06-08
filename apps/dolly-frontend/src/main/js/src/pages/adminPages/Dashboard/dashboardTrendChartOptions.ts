import { type Options } from 'highcharts'
import {
	ERROR_PRIMARY_COLOR,
	ERROR_SECONDARY_COLOR,
	getChartBaseOptions,
	TOOLTIP_OPTIONS,
} from './dashboardChartBase'
import { GenericTrendSeries, MonthlyTrendPoint, PersonTrendPoint } from './dashboardUtils'

type PersonTrendVisibilityOptions = {
	personerTotaltVisible?: boolean
	feilTotaltVisible?: boolean
	onPersonerTotaltVisibilityChange?: (visible: boolean) => void
	onFeilTotaltVisibilityChange?: (visible: boolean) => void
}

export const createPersonTrendChartOptions = (
	personTrendData: PersonTrendPoint[],
	visibilityOptions?: PersonTrendVisibilityOptions,
): Options => {
	return {
		...getChartBaseOptions(
			'Linjediagram med daglig utvikling av nye, gjenopprettede, PDL-feil og andre feil i valgt periode.',
		),
		chart: {
			...getChartBaseOptions('').chart,
			type: 'line',
			height: 360,
			marginBottom: 56,
		},
		xAxis: {
			categories: personTrendData.map((point) => point.datoVisning),
			title: { text: undefined },
			labels: {
				reserveSpace: true,
				autoRotation: [-45, -70],
				autoRotationLimit: 80,
				overflow: 'justify',
				style: {
					fontSize: '12px',
				},
			},
		},
		yAxis: {
			title: { text: undefined },
			allowDecimals: false,
			min: 0,
		},
		legend: {
			enabled: true,
			layout: 'horizontal',
			align: 'left',
			verticalAlign: 'top',
			itemMarginBottom: 4,
		},
		tooltip: { ...TOOLTIP_OPTIONS, shared: true },
		plotOptions: {
			series: {
				showInLegend: true,
				marker: {
					enabled: true,
					radius: 3,
				},
			},
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
	}
}

export const createMonthlyTeamTrendChartOptions = (
	teamTrendData: MonthlyTrendPoint[],
	options?: {
		description?: string
		secondSeriesName?: string
	},
): Options => ({
	...getChartBaseOptions(
		options?.description ||
			'Linjediagram med månedlig utvikling i unike brukere og antall aktive teams.',
	),
	chart: {
		...getChartBaseOptions('').chart,
		type: 'line',
		height: 360,
		marginBottom: 56,
	},
	xAxis: {
		categories: teamTrendData.map((point) => point.intervalVisning),
		title: { text: undefined },
		labels: {
			reserveSpace: true,
			autoRotation: [-45, -70],
			autoRotationLimit: 80,
			overflow: 'justify',
			style: {
				fontSize: '12px',
			},
		},
	},
	yAxis: {
		title: { text: undefined },
		allowDecimals: false,
	},
	legend: {
		enabled: false,
	},
	tooltip: { ...TOOLTIP_OPTIONS, shared: true },
	plotOptions: {
		series: {
			showInLegend: false,
		},
	},
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

export const createGenericTrendChartOptions = ({
	description,
	xAxisTitle,
	labels,
	series,
}: {
	description: string
	xAxisTitle?: string
	labels: string[]
	series: GenericTrendSeries[]
}): Options => ({
	...getChartBaseOptions(description),
	chart: {
		...getChartBaseOptions('').chart,
		type: 'line',
		height: 360,
		marginBottom: 56,
	},
	xAxis: {
		categories: labels,
		title: { text: xAxisTitle || undefined },
		labels: {
			reserveSpace: true,
			autoRotation: [-45, -70],
			autoRotationLimit: 80,
			overflow: 'justify',
			style: {
				fontSize: '12px',
			},
		},
	},
	yAxis: {
		title: { text: undefined },
		allowDecimals: false,
		min: 0,
	},
	legend: {
		enabled: true,
		layout: 'horizontal',
		align: 'left',
		verticalAlign: 'top',
		itemMarginBottom: 4,
	},
	tooltip: { ...TOOLTIP_OPTIONS, shared: true },
	plotOptions: {
		series: {
			showInLegend: true,
			marker: {
				enabled: true,
				radius: 3,
			},
		},
	},
	series: series.map((seriesItem) => ({
		type: 'line',
		name: seriesItem.name,
		data: seriesItem.data,
	})),
})
