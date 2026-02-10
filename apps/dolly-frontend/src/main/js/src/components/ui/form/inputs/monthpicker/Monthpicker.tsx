import { Label } from '@/components/ui/form/inputs/label/Label'
import { MonthPicker, useMonthpicker } from '@navikt/ds-react'
import { addYears, isDate, subYears } from 'date-fns'
import { useFormContext } from 'react-hook-form'
import * as _ from 'lodash-es'
import styled from 'styled-components'

interface MonthpickerProps {
	name: string
	label: string
	date?: Date
	handleDateChange?: (dato: string, type: string) => void
	onChange?: (date: Date) => void
	minDate?: Date
	maxDate?: Date
}

const MonthpickerWrapper = styled.div`
	margin-right: 20px;

	&& {
		.aksel-form-field {
			gap: 0;
		}
	}

	&&&& {
		input {
			width: 220px;
			height: 38px;
		}
	}
`

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
	const errorOutput = formMethods.getFieldState(name)?.error?.message

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
		<MonthpickerWrapper>
			<Label name={name} label={label}>
				<MonthPicker {...monthpickerProps} dropdownCaption={true} selected={formattedDate}>
					<MonthPicker.Input
						error={!!errorOutput}
						label={null}
						size={'small'}
						placeholder={'MM.YYYY'}
						{...inputProps}
					/>
				</MonthPicker>
			</Label>
		</MonthpickerWrapper>
	)
}
