import styled from 'styled-components'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'
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
	ukjentLandKode: string
	label: string
	kodeverk: string
}

export const LandVelger = ({
	formMethods,
	path,
	ukjentLandKode,
	label,
	kodeverk,
}: LandVelgerTypes) => {
	const [ukjentIsChecked, setUkjentIsChecked] = useBoolean(false)

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
				isClearable={false}
				isDisabled={ukjentIsChecked}
			/>
			<FormCheckbox
				name="ukjentLand"
				label="Ukjent land"
				checkboxMargin
				wrapperSize="tight"
				afterChange={(val: boolean) => handleUkjentLandChange(val)}
			/>
		</SelectWithCheckbox>
	)
}
