import Highcharts, { type Options } from 'highcharts'
import { Alert, Box, Button, Heading, HGrid, HStack, Label, VStack } from '@navikt/ds-react'
import { HighchartsReact } from 'highcharts-react-official'
import { type ReactNode, useEffect, useMemo, useRef } from 'react'

export type DashboardSelectOption = {
	value: string
	label: string
}

export const DashboardSelectButtons = ({
	label,
	options,
	selected,
	onSelect,
}: {
	label?: string
	options: DashboardSelectOption[]
	selected: string | null
	onSelect: (value: string) => void
}) => {
	const buttons = (
		<HStack gap="space-8" wrap>
			{options.map((option) => (
				<Button
					key={option.value}
					variant={selected === option.value ? 'secondary' : 'tertiary'}
					size="small"
					onClick={() => onSelect(option.value)}
				>
					{option.label}
				</Button>
			))}
		</HStack>
	)

	if (!label) {
		return buttons
	}

	return (
		<VStack gap="space-8">
			<Label>{label}</Label>
			{buttons}
		</VStack>
	)
}

export const DashboardSectionCard = ({ children }: { children: ReactNode }) => (
	<Box
		background="default"
		paddingInline={{ xs: 'space-16', md: 'space-20' }}
		borderRadius="8"
		borderWidth="1"
		borderColor="neutral-subtle"
		minWidth="0"
		style={{ paddingTop: '10px', paddingBottom: '5px' }}
	>
		{children}
	</Box>
)

export const DashboardKpiCard = ({ label, value }: { label: string; value: ReactNode }) => (
	<Box
		background="neutral-soft"
		padding={{ xs: 'space-12', md: 'space-16' }}
		borderRadius="8"
		borderWidth="1"
		borderColor="neutral-subtle"
	>
		<VStack gap="space-4">
			<Label size="small">{label}</Label>
			{typeof value === 'number' || typeof value === 'string' ? (
				<Heading size="medium" level="3">
					{value}
				</Heading>
			) : (
				value
			)}
		</VStack>
	</Box>
)

export const DashboardChartPanel = ({
	options,
	ariaLabel,
	isLoading = false,
}: {
	options: Options
	ariaLabel: string
	isLoading?: boolean
}) => {
	const chartRef = useRef<HighchartsReact.RefObject>(null)
	const chartHeightOption = options.chart?.height
	const containerHeight =
		typeof chartHeightOption === 'number'
			? `${chartHeightOption}px`
			: typeof chartHeightOption === 'string'
				? chartHeightOption
				: '320px'

	const optionsSignature = JSON.stringify(options)
	const stableOptions = useMemo(() => options, [optionsSignature])

	useEffect(() => {
		const chart = chartRef.current?.chart
		if (!chart) return
		if (isLoading) {
			chart.showLoading()
		} else {
			chart.hideLoading()
		}
	}, [isLoading])

	return (
		<Box as="section" aria-label={ariaLabel}>
			<Box width="100%">
				<HighchartsReact
					ref={chartRef}
					highcharts={Highcharts}
					options={stableOptions}
					immutable
					containerProps={{ style: { width: '100%', height: containerHeight } }}
				/>
			</Box>
		</Box>
	)
}

export const DashboardHeaderActions = ({
	mockModeEnabled,
	onSeedMockData,
	onShowRealData,
}: {
	mockModeEnabled: boolean
	onSeedMockData: () => void
	onShowRealData: () => void
}) => (
	<>
		<HGrid columns={{ xs: 1, sm: 2, md: 3 }} gap="space-12" align="end">
			<Button variant="secondary" onClick={onSeedMockData}>
				Fyll med testdata
			</Button>
			<Button variant="secondary" disabled={!mockModeEnabled} onClick={onShowRealData}>
				Vis ekte data
			</Button>
		</HGrid>
		{mockModeEnabled && (
			<Alert variant="warning" inline>
				Testdata er aktiv. Bruk «Vis ekte data» for å vise reell data.
			</Alert>
		)}
	</>
)
