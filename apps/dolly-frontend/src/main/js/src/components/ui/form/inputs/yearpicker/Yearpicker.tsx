import _ from 'lodash'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import ReactDatepicker from 'react-datepicker'
import { TextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface YearpickerProps {
	formMethods: UseFormReturn
	name: string
	label: string
	date: Date
	handleDateChange: (dato: string, type: string) => void
	minDate?: Date
	maxDate?: Date
	disabled?: boolean
}

export const Yearpicker = ({
	formMethods,
	name,
	label,
	date,
	handleDateChange,
	maxDate = null,
	minDate = null,
	disabled = false,
}: YearpickerProps) => {
	const getFeilmelding = (formMethods: UseFormReturn, formPath: string) => {
		const feilmelding = _.get(formMethods.formState.errors, formPath)
		return feilmelding ? { feilmelding: feilmelding } : null
	}

	return (
		<InputWrapper>
			<Label name={name} label={label} feil={getFeilmelding(formMethods, name)}>
				<ReactDatepicker
					name={name}
					className={'skjemaelement__input'}
					dateFormat="yyyy"
					selected={date}
					onChange={handleDateChange}
					placeholderText={'Ikke spesifisert'}
					showYearPicker
					customInput={
						<TextInput
							isDisabled={disabled}
							name={name}
							icon="calendar"
							feil={getFeilmelding(formMethods, name)}
						/>
					}
					maxDate={maxDate}
					minDate={minDate}
					disabled={disabled}
				/>
			</Label>
		</InputWrapper>
	)
}
