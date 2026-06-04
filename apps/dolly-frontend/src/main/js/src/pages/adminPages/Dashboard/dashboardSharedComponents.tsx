import Highcharts, { type Options } from 'highcharts'
import { Alert, Box, Button, Heading, HGrid, Label, VStack } from '@navikt/ds-react'
import { HighchartsReact } from 'highcharts-react-official'
import type { ReactNode } from 'react'

export const DashboardSectionCard = ({ children }: { children: ReactNode }) => (
	<Box
		background="default"
		padding={{ xs: 'space-16', md: 'space-20' }}
		borderRadius="8"
		borderWidth="1"
		borderColor="neutral-subtle"
		minWidth="0"
	>
		{children}
	</Box>
)

export const DashboardKpiCard = ({ label, value }: { label: string; value: number }) => (
	<Box
		background="neutral-soft"
		padding={{ xs: 'space-12', md: 'space-16' }}
		borderRadius="8"
		borderWidth="1"
		borderColor="neutral-subtle"
	>
		<VStack gap="space-4">
			<Label size="small">{label}</Label>
			<Heading size="medium" level="3">
				{value}
			</Heading>
		</VStack>
	</Box>
)

export const DashboardChartPanel = ({
	options,
	ariaLabel,
}: {
	options: Options
	ariaLabel: string
}) => (
	<Box as="section" aria-label={ariaLabel}>
		<Box width="100%" height="320px">
			<HighchartsReact
				highcharts={Highcharts}
				options={options}
				containerProps={{ style: { width: '100%', height: '100%' } }}
			/>
		</Box>
	</Box>
)

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
