import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { MonthPicker, useMonthpicker } from '@navikt/ds-react'
import { addYears, isDate, subYears } from 'date-fns'
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
	const val = formMethods.watch(name)
	// console.log('val: ', val) //TODO - SLETT MEG
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
			console.log('selectedDate: ', selectedDate) //TODO - SLETT MEG
			selectedDate?.setHours(12)
			onChange ? onChange(selectedDate) : handleDateChange(selectedDate)
		},
		defaultSelected: !_.isNil(formattedDate)
			? isDate(formattedDate)
				? formattedDate
				: new Date(formattedDate)
			: undefined,
	})
	// console.log('name: ', name) //TODO - SLETT MEG
	// console.log('date: ', date) //TODO - SLETT MEG
	// console.log('formattedDate: ', formattedDate) //TODO - SLETT MEG

	if (!val && inputProps.value) {
		reset()
	}
	// TODO sjekk om reset funker med oppdatert getUpdatedRequest

	if (name === 'tjenestepensjonsavtale.periode') {
		console.log('monthpickerProps: ', monthpickerProps) //TODO - SLETT MEG
		console.log('inputProps: ', inputProps) //TODO - SLETT MEG
	}
	return (
		<InputWrapper size={'small'}>
			<Label name={name} label={label}>
				<MonthPicker {...monthpickerProps} dropdownCaption={true} selected={formattedDate}>
					<MonthPicker.Input label={null} size={'small'} placeholder={'yyyy-mm'} {...inputProps} />
				</MonthPicker>
			</Label>
		</InputWrapper>
	)
}
