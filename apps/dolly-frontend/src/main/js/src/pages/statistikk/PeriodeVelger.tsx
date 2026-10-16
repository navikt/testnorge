import { BodyShort, Box, Button, HStack, Label, ToggleGroup, VStack } from '@navikt/ds-react'
import { useId } from 'react'
import { type DashboardSelectOption } from '@/pages/adminPages/Dashboard/dashboardSharedComponents'
import { aarSammendragTittel, erPeriodeVisning, type PeriodeVisning } from './brukerStatistikkUtils'

interface PeriodeKnapperProps {
	label: string
	options: DashboardSelectOption[]
	selected: string | null
	inkluderte?: string[]
	onSelect: (value: string) => void
}

const PeriodeKnapper = ({
	label,
	options,
	selected,
	inkluderte = [],
	onSelect,
}: PeriodeKnapperProps) => {
	const labelId = useId()
	return (
		<VStack gap="space-8">
			<Label as="span" id={labelId}>
				{label}
			</Label>
			<HStack gap="space-8" wrap role="group" aria-labelledby={labelId}>
				{options.map((option) => {
					const erValgt = option.value === selected
					const erInkludert = !erValgt && inkluderte.includes(option.value)
					return (
						<Button
							key={option.value}
							variant={erValgt || erInkludert ? 'secondary' : 'tertiary'}
							data-color={erInkludert ? 'neutral' : undefined}
							size="small"
							aria-pressed={erValgt}
							onClick={() => onSelect(option.value)}
						>
							{option.label}
							{erInkludert && (
								<BodyShort as="span" visuallyHidden>
									, med i tidsrommet
								</BodyShort>
							)}
						</Button>
					)
				})}
			</HStack>
		</VStack>
	)
}

interface PeriodeVelgerProps {
	aarOptions: string[]
	valgtAar: string
	onAarChange: (aar: string) => void
	maanedOptions: DashboardSelectOption[]
	valgtMaaned: string | null
	onMaanedChange: (maaned: string) => void
	visning: PeriodeVisning
	onVisningChange: (visning: PeriodeVisning) => void
}

export const PeriodeVelger = ({
	aarOptions,
	valgtAar,
	onAarChange,
	maanedOptions,
	valgtMaaned,
	onMaanedChange,
	visning,
	onVisningChange,
}: PeriodeVelgerProps) => {
	const erAarsvisning = visning === 'aar'
	return (
		<VStack gap="space-16">
			<VStack gap="space-8">
				<PeriodeKnapper
					label="År"
					selected={valgtAar}
					onSelect={onAarChange}
					options={aarOptions.map((aar) => ({ value: aar, label: aar }))}
				/>
				<PeriodeKnapper
					label="Måned"
					selected={erAarsvisning ? null : valgtMaaned}
					inkluderte={erAarsvisning ? maanedOptions.map((option) => option.value) : []}
					onSelect={onMaanedChange}
					options={maanedOptions}
				/>
			</VStack>
			<Box maxWidth="20rem">
				<ToggleGroup
					label="Vis statistikk for"
					size="small"
					fill
					value={visning}
					onChange={(verdi) => {
						if (erPeriodeVisning(verdi)) {
							onVisningChange(verdi)
						}
					}}
				>
					<ToggleGroup.Item value="maaned" label="Kun valgt måned" />
					<ToggleGroup.Item value="aar" label={aarSammendragTittel(Number(valgtAar))} />
				</ToggleGroup>
			</Box>
		</VStack>
	)
}
