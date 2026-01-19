import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { MonthPicker, useMonthpicker } from '@navikt/ds-react'
import { addYears, isDate, isSameDay, subYears } from 'date-fns'
import { useFormContext } from 'react-hook-form'
import * as _ from 'lodash-es'

interface MonthpickerProps {
	name: string
	label: string
	date?: Date
	handleDateChange?: (dato: string, type: string) => void
	onChange?: (date: Date) => void
	minDate?: Date
	maxDate?: Date
}

export const Monthpicker = ({
	name,
	label,
	date = null,
	handleDateChange,
	onChange,
	minDate = null,
	maxDate = null,
}: MonthpickerProps) => {
	const formMethods = useFormContext()
	const eksisterendeVerdi = formMethods.watch(name)
	const errorOutput= formMethods.getFieldState(name)?.error?.message

	const formattedDate =
		eksisterendeVerdi instanceof Date
			? eksisterendeVerdi
			: date instanceof Date || date === null
				? date
				: new Date(date)

	const { monthpickerProps, inputProps } = useMonthpicker({
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

	return (
		<InputWrapper size={'small'}>
			<Label name={name} label={label}>
				<MonthPicker {...monthpickerProps} dropdownCaption={true} selected={formattedDate}>
					<MonthPicker.Input error={!!errorOutput} label={null} size={'small'} placeholder={'MM.YYYY'} {...inputProps} />
				</MonthPicker>
			</Label>
		</InputWrapper>
	)
}
