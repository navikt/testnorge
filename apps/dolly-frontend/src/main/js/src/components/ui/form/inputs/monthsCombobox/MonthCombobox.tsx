import { useRef, useState } from 'react'
import { useFormContext } from 'react-hook-form'
import styled from 'styled-components'
import {
	Button,
	HStack,
	MonthPicker,
	Popover,
	UNSAFE_Combobox as Combobox,
	VStack,
} from '@navikt/ds-react'
import { CalendarIcon } from '@navikt/aksel-icons'
import { addYears, format, isValid, parse, subYears } from 'date-fns'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { formatMonth } from '@/utils/DataFormatter'

const VALUE_FORMAT = 'yyyy-MM'
const MONTHS_PER_ROW = 4
const NO_SELECTED_MONTH = new Date(NaN)

interface MonthsComboboxProps {
	name: string
	label: string
	minDate?: Date
	maxDate?: Date
}

interface MonthOption {
	label: string
	value: string
}

const SelectedMonthsWrapper = styled.div<{ $selectedMonthIndexes: number[] }>`
	.aksel-date {
		padding: 5px 0;
	}

	${({ $selectedMonthIndexes }) =>
		$selectedMonthIndexes
			.map(
				(monthIndex) => `
					.rdp-tbody tr:nth-child(${Math.floor(monthIndex / MONTHS_PER_ROW) + 1})
						td:nth-child(${(monthIndex % MONTHS_PER_ROW) + 1})
						.rdp-day__inner {
						color: var(--ax-text-contrast);
						background: var(--ax-bg-strong-pressed);
					}
				`,
			)
			.join('')}
`

const ComboboxWrapper = styled.div`
	position: relative;

	.aksel-combobox__wrapper-inner.aksel-text-field__input {
		padding-inline-end: 2.75rem;
	}
`

const CalendarButton = styled(Button)`
	position: absolute;
	top: 0.5rem;
	right: 0.5rem;
`

const parseMonth = (value: string) => parse(value, VALUE_FORMAT, new Date())

const toMonthOption = (value: string): MonthOption => ({
	value,
	label: formatMonth(value),
})

const getValidMonths = (values: unknown): string[] =>
	Array.isArray(values)
		? values.filter(
				(value): value is string => typeof value === 'string' && isValid(parseMonth(value)),
			)
		: []

export const MonthCombobox = ({ name, label, minDate, maxDate }: MonthsComboboxProps) => {
	const formMethods = useFormContext()
	const anchorRef = useRef<HTMLDivElement>(null)
	const [isOpen, setIsOpen] = useState(false)
	const [visibleYear, setVisibleYear] = useState(() => new Date())
	const [defaultDateRange] = useState(() => ({
		fromDate: subYears(new Date(), 125),
		toDate: addYears(new Date(), 5),
	}))

	const selectedMonths = getValidMonths(formMethods.watch(name))
	const selectedOptions = selectedMonths.map(toMonthOption)
	const errorMessage = formMethods.getFieldState(name)?.error?.message

	const selectedMonthIndexesInVisibleYear = selectedMonths
		.map(parseMonth)
		.filter((month) => month.getFullYear() === visibleYear.getFullYear())
		.map((month) => month.getMonth())

	const updateMonths = (months: string[]) => {
		formMethods.setValue(name, [...months].sort(), { shouldTouch: true })
		formMethods.trigger(name)
	}

	const toggleMonth = (month: string) => {
		if (selectedMonths.includes(month)) {
			updateMonths(selectedMonths.filter((selectedMonth) => selectedMonth !== month))
			return
		}
		updateMonths([...selectedMonths, month])
	}

	const togglePopover = () => {
		if (!isOpen) {
			const latestSelectedMonth = selectedMonths.at(-1)
			setVisibleYear(latestSelectedMonth ? parseMonth(latestSelectedMonth) : new Date())
		}
		setIsOpen(!isOpen)
	}

	return (
		<Label name={name} label={label} containerClass="flexbox--full-width">
			<ComboboxWrapper ref={anchorRef}>
				<Combobox
					id={name}
					label={label}
					hideLabel
					isMultiSelect
					isListOpen={false}
					toggleListButton={false}
					options={selectedOptions}
					selectedOptions={selectedOptions}
					onToggleSelected={(month) => toggleMonth(month)}
					placeholder={selectedOptions.length > 0 ? undefined : 'Velg måneder ...'}
					error={!!errorMessage}
				/>
				<CalendarButton
					type="button"
					size="small"
					variant="tertiary-neutral"
					icon={<CalendarIcon title={isOpen ? 'Lukk månedsvelger' : 'Åpne månedsvelger'} />}
					onClick={togglePopover}
					aria-expanded={isOpen}
				/>
			</ComboboxWrapper>
			<Popover
				open={isOpen}
				onClose={() => setIsOpen(false)}
				anchorEl={anchorRef.current}
				placement="bottom-end"
			>
				<Popover.Content>
					<VStack gap="space-8">
						<SelectedMonthsWrapper $selectedMonthIndexes={selectedMonthIndexesInVisibleYear}>
							<MonthPicker.Standalone
								dropdownCaption
								fromDate={minDate ?? defaultDateRange.fromDate}
								toDate={maxDate ?? defaultDateRange.toDate}
								year={visibleYear}
								selected={NO_SELECTED_MONTH}
								onYearChange={(year) => year && setVisibleYear(year)}
								onMonthSelect={(month) => month && toggleMonth(format(month, VALUE_FORMAT))}
							/>
						</SelectedMonthsWrapper>
						<HStack justify="end">
							<Button variant="secondary" size="small" onClick={() => setIsOpen(false)}>
								Ferdig
							</Button>
						</HStack>
					</VStack>
				</Popover.Content>
			</Popover>
		</Label>
	)
}
