import { type Options } from 'highcharts'
import { TOOLTIP_OPTIONS, withBaseChart } from './dashboardChartBase'

type DonutSlice = {
	name: string
	y: number
	color: string
}

const createDonutChartOptions = (
	description: string,
	seriesName: string,
	slices: DonutSlice[],
	height: number,
	insideLabels = false,
): Options => ({
	...withBaseChart(description, { type: 'pie', height }),
	legend: {
		enabled: true,
		itemStyle: { fontSize: '12px' },
	},
	plotOptions: {
		pie: {
			innerSize: '60%',
			showInLegend: true,
			...(insideLabels
				? {
						size: '90%',
						dataLabels: {
							enabled: true,
							distance: -28,
							format: '{point.y}',
							style: {
								fontSize: '12px',
								fontWeight: 'bold',
								textOutline: 'none',
								color: '#ffffff',
							},
						},
					}
				: {
						dataLabels: {
							enabled: true,
							format: '<b>{point.name}</b>: {point.y}',
							style: { fontSize: '12px' },
						},
					}),
		},
	},
	tooltip: {
		...TOOLTIP_OPTIONS,
		shared: false,
		headerFormat: '',
		pointFormat: '<b>{point.name}:</b> {point.y} ({point.percentage:.1f} %)',
	},
	series: [
		{
			type: 'pie',
			name: seriesName,
			data: slices,
		},
	],
})

export const createOpprettetGjenopprettetDonutChartOptions = ({
	nye,
	gjenopprettede,
}: {
	nye: number
	gjenopprettede: number
}): Options =>
	createDonutChartOptions(
		'Sirkeldiagram som viser fordelingen mellom nye og gjenopprettede bestillinger for valgt dag.',
		'Bestillinger',
		[
			{ name: 'Nye bestillinger', y: nye, color: 'var(--ax-success-700)' },
			{ name: 'Gjenopprettede bestillinger', y: gjenopprettede, color: 'var(--ax-accent-700)' },
		],
		400,
		true,
	)

export const createNyeGjenopprettedeDonutChartOptions = ({
	nye,
	gjenopprettede,
}: {
	nye: number
	gjenopprettede: number
}): Options =>
	createDonutChartOptions(
		'Sirkeldiagram som viser fordelingen mellom nye og gjenopprettede bestillinger i valgt periode.',
		'Bestillinger',
		[
			{ name: 'Nye bestillinger', y: nye, color: 'var(--ax-success-700)' },
			{ name: 'Gjenopprettede bestillinger', y: gjenopprettede, color: 'var(--ax-accent-700)' },
		],
		400,
		true,
	)

export const createIdenterDonutChartOptions = ({
	dollyPersoner,
	testnorgePersoner,
}: {
	dollyPersoner: number
	testnorgePersoner: number
}): Options =>
	createDonutChartOptions(
		'Sirkeldiagram som viser fordelingen av nye personer mellom personer opprettet i Dolly og personer importert fra Testnorge.',
		'Nye personer',
		[
			{ name: 'Opprettet i Dolly', y: dollyPersoner, color: 'var(--ax-accent-700)' },
			{ name: 'Importert fra Testnorge', y: testnorgePersoner, color: 'var(--ax-success-700)' },
		],
		400,
		true,
	)
