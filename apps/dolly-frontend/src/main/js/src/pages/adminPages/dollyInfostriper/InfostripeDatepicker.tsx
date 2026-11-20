import React from 'react'
import { Control, useController } from 'react-hook-form'
import { DatePicker, useDatepicker } from '@navikt/ds-react'
import { InfostripeFormValues } from '@/pages/adminPages/dollyInfostriper/CreateInfostripeForm'

interface InfostripeDatepickerProps {
	name: 'start' | 'expires'
	label: string
	control: Control<InfostripeFormValues>
	required?: boolean
}

export const InfostripeDatepicker: React.FC<InfostripeDatepickerProps> = ({
	name,
	label,
	control,
	required = true,
}) => {
	const { field, fieldState } = useController({
		name,
		control,
		rules: { required: required ? 'Påkrevd' : false },
	})

	const { datepickerProps, inputProps } = useDatepicker({
		defaultSelected: field.value,
		onDateChange: (date) => field.onChange(date),
	})

	return (
		<DatePicker {...datepickerProps}>
			<DatePicker.Input
				{...inputProps}
				label={label}
				onBlur={(event) => {
					inputProps.onBlur?.(event)
					field.onBlur()
				}}
				error={fieldState.error?.message}
			/>
		</DatePicker>
	)
}
