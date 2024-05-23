import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { MonthPicker, useMonthpicker } from '@navikt/ds-react'
import { addYears, isDate, subYears } from 'date-fns'
import { useFormContext } from 'react-hook-form'
import _ from 'lodash'
import { useEffect } from 'react'

interface MonthpickerProps {
	name: string
	label: string
	date?: Date
	handleDateChange?: (dato: string, type: string) => void
	onChange?: (date: Date) => void
	isClearable?: boolean
	minDate?: Date
	maxDate?: Date
	placeholder?: string
}

export const Monthpicker = ({
	name,
	label,
	date = null,
	handleDateChange,
	onChange,
	isClearable = false,
	minDate = null,
	maxDate = null,
	placeholder = null,
	...props
}: MonthpickerProps) => {
	const formMethods = useFormContext()
	const val = formMethods.watch(name)

	function getEksisterendeVerdi() {
		if (name.includes('navArbeidsforholdPeriode')) {
			return val?.year ? new Date(val?.year, val?.monthValue) : null
		}
		return val
	}

	const eksisterendeVerdi = getEksisterendeVerdi()

	const formattedDate =
		eksisterendeVerdi instanceof Date
			? eksisterendeVerdi
			: date instanceof Date || date === null
				? date
				: new Date(date)

	const { monthpickerProps, inputProps, reset } = useMonthpicker({
		fromDate: minDate || subYears(new Date(), 125),
		toDate: maxDate || addYears(new Date(), 5),
		onMonthChange: (selectedDate) => {
			selectedDate?.setHours(12)
			onChange ? onChange(selectedDate) : handleDateChange(selectedDate)
		},
		defaultSelected: !_.isNil(formattedDate)
			? isDate(formattedDate)
				? formattedDate
				: new Date(formattedDate)
			: undefined,
	})

	useEffect(() => {
		if (!val) {
			onChange ? onChange(null) : handleDateChange(null)
			reset()
		}
	}, [val])

	return (
		<InputWrapper size={'small'}>
			<Label name={name} label={label}>
				<MonthPicker {...monthpickerProps} dropdownCaption={true} selected={formattedDate}>
					<MonthPicker.Input
						label={null}
						size={'small'}
						placeholder={placeholder || 'yyyy-mm'}
						{...inputProps}
					/>
				</MonthPicker>
			</Label>
		</InputWrapper>
	)
}
