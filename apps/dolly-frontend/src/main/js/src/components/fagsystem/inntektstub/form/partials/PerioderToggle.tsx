import { useState } from 'react'
import styled from 'styled-components'
import { ToggleGroup } from '@navikt/ds-react'
import { useFormContext } from 'react-hook-form'
import { eachMonthOfInterval, format, isAfter, parse } from 'date-fns'
import { MonthCombobox } from '@/components/ui/form/inputs/monthsCombobox/MonthCombobox'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { Label } from '@/components/ui/form/inputs/label/Label'

const VALUE_FORMAT = 'yyyy-MM'

enum PeriodeValg {
	ENKELTMAANEDER = 'ENKELTMAANEDER',
	PERIODE = 'PERIODE',
}

const StyledToggleGroup = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
	margin: 10px 0 10px 0;
`

interface PerioderToggleProps {
	path: string
}

const parseMonth = (value?: string) => (value ? parse(value, VALUE_FORMAT, new Date()) : null)

export const PerioderToggle = ({ path }: PerioderToggleProps) => {
	const formMethods = useFormContext()
	const perioderPath = `${path}.perioder`
	const eksisterendePerioder: string[] = formMethods.getValues(perioderPath) ?? []

	const [periodeValg, setPeriodeValg] = useState(PeriodeValg.ENKELTMAANEDER)
	const [fraMaaned, setFraMaaned] = useState<Date | null>(() => parseMonth(eksisterendePerioder[0]))
	const [tilMaaned, setTilMaaned] = useState<Date | null>(() =>
		parseMonth(eksisterendePerioder.at(-1)),
	)

	const oppdaterPerioder = (fra: Date | null, til: Date | null) => {
		if (!fra || !til || isAfter(fra, til)) {
			formMethods.setValue(perioderPath, [], { shouldTouch: true })
		} else {
			const maaneder = eachMonthOfInterval({ start: fra, end: til }).map((maaned) =>
				format(maaned, VALUE_FORMAT),
			)
			formMethods.setValue(perioderPath, maaneder, { shouldTouch: true })
		}
		formMethods.trigger(perioderPath)
	}

	const handleFraChange = (dato?: Date) => {
		setFraMaaned(dato ?? null)
		oppdaterPerioder(dato ?? null, tilMaaned)
	}

	const handleTilChange = (dato?: Date) => {
		setTilMaaned(dato ?? null)
		oppdaterPerioder(fraMaaned, dato ?? null)
	}

	const ugyldigPeriode = fraMaaned && tilMaaned && isAfter(fraMaaned, tilMaaned)

	return (
		<div className="toggle--wrapper">
			<StyledToggleGroup
				onChange={(value) => setPeriodeValg(value as PeriodeValg)}
				value={periodeValg}
				size="small"
			>
				<ToggleGroup.Item value={PeriodeValg.ENKELTMAANEDER}>Velg enkeltmåneder</ToggleGroup.Item>
				<ToggleGroup.Item value={PeriodeValg.PERIODE}>Velg periode</ToggleGroup.Item>
			</StyledToggleGroup>
			{periodeValg === PeriodeValg.ENKELTMAANEDER ? (
				<MonthCombobox name={perioderPath} label="Perioder" />
			) : (
				<Label
					name={perioderPath}
					label=""
					containerClass="flexbox--full-width"
					manualError={ugyldigPeriode ? 'Fra-måned kan ikke være etter til-måned' : undefined}
				>
					<div className="flexbox--flex-wrap">
						<Monthpicker
							name={`${path}.periodeFra`}
							label="F.o.m. måned"
							date={fraMaaned}
							onChange={handleFraChange}
						/>
						<Monthpicker
							name={`${path}.periodeTil`}
							label="T.o.m. måned"
							date={tilMaaned}
							onChange={handleTilChange}
						/>
					</div>
				</Label>
			)}
		</div>
	)
}
