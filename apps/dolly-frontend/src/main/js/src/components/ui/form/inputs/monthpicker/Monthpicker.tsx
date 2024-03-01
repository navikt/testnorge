import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { MonthPicker, useMonthpicker } from '@navikt/ds-react'

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
	const formattedDate = date instanceof Date || date === null ? date : new Date(date)

	const { monthpickerProps, inputProps, selectedMonth } = useMonthpicker({
		fromDate: minDate,
		toDate: maxDate,
		onMonthChange: onChange ? onChange : handleDateChange,
	})

	return (
		<InputWrapper size={'small'}>
			<Label name={name} label={label}>
				<MonthPicker dropdownCaption={true} selected={formattedDate} {...monthpickerProps}>
					<MonthPicker.Input
						label={null}
						size={'small'}
						// className={'skjemaelement__input'}
						aria-placeholder={'yyyy-mm'}
						{...inputProps}
					/>
				</MonthPicker>
			</Label>
		</InputWrapper>
	)
}
