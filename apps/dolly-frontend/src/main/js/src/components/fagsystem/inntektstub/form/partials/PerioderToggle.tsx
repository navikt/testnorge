import { useContext, useState } from 'react'
import styled from 'styled-components'
import { ToggleGroup, Tooltip } from '@navikt/ds-react'
import { LinkBrokenIcon } from '@navikt/aksel-icons'
import { useFormContext } from 'react-hook-form'
import { eachMonthOfInterval, format, isAfter, parse } from 'date-fns'
import { MonthCombobox } from '@/components/ui/form/inputs/monthsCombobox/MonthCombobox'
import { Monthpicker } from '@/components/ui/form/inputs/monthpicker/Monthpicker'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { ShowErrorContext } from '@/components/bestillingsveileder/ShowErrorContext'

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

const COMBOBOX_HOYDE = '3rem'
const MONTHPICKER_HOYDE = '38px'

const PeriodeInnhold = styled.div<{ $periodeValg: PeriodeValg }>`
	margin-bottom: ${({ $periodeValg }) =>
		$periodeValg === PeriodeValg.PERIODE
			? `calc(1rem + ${COMBOBOX_HOYDE} - ${MONTHPICKER_HOYDE})`
			: '1rem'};

	.skjemaelement {
		margin-bottom: 0;
	}
`

const ManglendeMaanederIkon = styled.span`
	display: flex;
	align-items: center;
	align-self: flex-end;
	height: 2rem;
	margin: 0 0.75rem 0.25rem -0.5rem;
	font-size: 1.5rem;
`

interface PerioderToggleProps {
	path: string
}

const parseMonth = (value?: string) => (value ? parse(value, VALUE_FORMAT, new Date()) : null)

const getSorterteMaaneder = (perioder: unknown): string[] =>
	Array.isArray(perioder) ? [...perioder].sort((a: string, b: string) => a.localeCompare(b)) : []

const harManglendeMaaneder = (perioder: string[]) => {
	const forsteMaaned = parseMonth(perioder[0])
	const sisteMaaned = parseMonth(perioder.at(-1))
	if (!forsteMaaned || !sisteMaaned) return false
	const antallMaanederIPerioden = eachMonthOfInterval({
		start: forsteMaaned,
		end: sisteMaaned,
	}).length
	return new Set(perioder).size < antallMaanederIPerioden
}

export const PerioderToggle = ({ path }: PerioderToggleProps) => {
	const formMethods = useFormContext()
	const errorContext = useContext(ShowErrorContext)
	const perioderPath = `${path}.perioder`
	const perioder = getSorterteMaaneder(formMethods.watch(perioderPath))

	const [periodeValg, setPeriodeValg] = useState(PeriodeValg.ENKELTMAANEDER)
	const [fraMaaned, setFraMaaned] = useState<Date | null>(null)
	const [tilMaaned, setTilMaaned] = useState<Date | null>(null)

	const handlePeriodeValgChange = (valg: PeriodeValg) => {
		if (valg === PeriodeValg.PERIODE) {
			setFraMaaned(parseMonth(perioder[0]))
			setTilMaaned(parseMonth(perioder.at(-1)))
		}
		setPeriodeValg(valg)
	}

	const oppdaterPerioder = (fra: Date | null, til: Date | null) => {
		if (!fra || !til || isAfter(fra, til)) {
			formMethods.setValue(perioderPath, [], { shouldTouch: true })
		} else {
			const maaneder = eachMonthOfInterval({ start: fra, end: til }).map((maaned) =>
				format(maaned, VALUE_FORMAT),
			)
			formMethods.setValue(perioderPath, maaneder, { shouldTouch: true })
		}
		void formMethods.trigger(perioderPath)
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

	const perioderFieldState = formMethods.getFieldState(perioderPath, formMethods.formState)
	const visPerioderFeil =
		!!perioderFieldState.error &&
		(errorContext?.showError || perioderFieldState.isTouched || formMethods.formState.isSubmitted)
	const fraMaanedFeil = !!ugyldigPeriode || (visPerioderFeil && !fraMaaned)
	const tilMaanedFeil = !!ugyldigPeriode || (visPerioderFeil && !tilMaaned)

	return (
		<div className="toggle--wrapper">
			<StyledToggleGroup
				onChange={(value) => handlePeriodeValgChange(value as PeriodeValg)}
				value={periodeValg}
				size="small"
			>
				<ToggleGroup.Item value={PeriodeValg.ENKELTMAANEDER}>Velg enkeltmåneder</ToggleGroup.Item>
				<ToggleGroup.Item value={PeriodeValg.PERIODE}>Velg periode</ToggleGroup.Item>
			</StyledToggleGroup>
			<PeriodeInnhold $periodeValg={periodeValg}>
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
								error={fraMaanedFeil}
							/>
							{harManglendeMaaneder(perioder) && (
								<Tooltip
									content="Perioden mangler enkelte måneder. Endre fra- eller til-måned for å fylle inn alle månedene i perioden."
									maxChar={150}
								>
									<ManglendeMaanederIkon tabIndex={0}>
										<LinkBrokenIcon aria-hidden />
									</ManglendeMaanederIkon>
								</Tooltip>
							)}
							<Monthpicker
								name={`${path}.periodeTil`}
								label="T.o.m. måned"
								date={tilMaaned}
								onChange={handleTilChange}
								error={tilMaanedFeil}
							/>
						</div>
					</Label>
				)}
			</PeriodeInnhold>
		</div>
	)
}
