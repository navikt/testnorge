import styled from 'styled-components'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { useBoolean } from 'react-use'
import { UseFormReturn } from 'react-hook-form/dist/types'

const SelectWithCheckbox = styled.div`
	display: flex;
	margin-right: 25px;
`

type LandVelgerTypes = {
	formMethods: UseFormReturn
	path: string
	checkboxName: string
	ukjentLandKode: string
	label: string
	kodeverk: string
	disabled?: boolean
	handleChangeSelect?: Function
	handleChangeCheckbox?: Function
}

export const LandVelger = ({
	formMethods,
	path,
	checkboxName,
	ukjentLandKode,
	label,
	kodeverk,
	disabled = false,
	handleChangeSelect,
	handleChangeCheckbox,
}: LandVelgerTypes) => {
	const [ukjentIsChecked, setUkjentIsChecked] = useBoolean(formMethods.getValues(checkboxName))

	const handleUkjentLandChange = (isChecked: boolean) => {
		setUkjentIsChecked(isChecked)
		formMethods?.setValue(path, isChecked ? ukjentLandKode : null)
	}

	return (
		<SelectWithCheckbox>
			<FormSelect
				name={path}
				label={label}
				kodeverk={kodeverk}
				size="large"
				isDisabled={disabled || ukjentIsChecked}
				onChange={handleChangeSelect}
			/>
			<FormCheckbox
				name={checkboxName}
				label="Ukjent land"
				checkboxMargin
				wrapperSize="tight"
				afterChange={(val: boolean) => {
					handleUkjentLandChange(val)
					if (val && handleChangeCheckbox) {
						handleChangeCheckbox()
					}
				}}
				disabled={disabled}
			/>
		</SelectWithCheckbox>
	)
}
