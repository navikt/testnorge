import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { MonthPicker, useMonthpicker } from '@navikt/ds-react'
import { addYears, subYears } from 'date-fns'
import { useFormContext } from 'react-hook-form'
import _ from 'lodash'

interface MonthpickerProps {
	name: string
	label: string
	date?: Date
	handleDateChange?: (dato: string, type: string) => void
	onChange?: (date: Date) => void
	isClearable?: boolean
	minDate?: Date
	maxDate?: Date
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
	...props
}: MonthpickerProps) => {
	const formMethods = useFormContext()
	const eksisterendeVerdi = formMethods.watch(name)

	const formattedDate = date instanceof Date || date === null ? date : new Date(date)

	const { monthpickerProps, inputProps, selectedMonth } = useMonthpicker({
		fromDate: minDate || subYears(new Date(), 125),
		toDate: maxDate || addYears(new Date(), 5),
		onMonthChange: onChange ? onChange : handleDateChange,
		defaultSelected: !_.isEmpty(eksisterendeVerdi) ? new Date(eksisterendeVerdi) : undefined,
	})

	return (
		<InputWrapper size={'small'}>
			<Label name={name} label={label}>
				<MonthPicker dropdownCaption={true} selected={formattedDate} {...monthpickerProps}>
					<MonthPicker.Input label={null} size={'small'} placeholder={'yyyy-mm'} {...inputProps} />
				</MonthPicker>
			</Label>
		</InputWrapper>
	)
}
