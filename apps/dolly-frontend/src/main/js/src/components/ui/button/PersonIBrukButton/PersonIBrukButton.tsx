import React, { SyntheticEvent, useState } from 'react'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import styled from 'styled-components'

const StyledSwitch = styled(DollyCheckbox)`
	.navds-switch__input:checked ~ .navds-switch__track {
		background-color: #0067c5;
	}

	.navds-switch__input:checked ~ .navds-switch__track > .navds-switch__thumb {
		color: #0067c5;
	}
`

export const PersonIBrukButton = ({ ident, updateIdentIbruk, ...props }) => {
	const [brukt, setBrukt] = useState(ident.ibruk)
	const handleOnChange = (event: any) => {
		const erIBruk = event.target.checked
		setBrukt(erIBruk)
		updateIdentIbruk(ident.ident, erIBruk)
	}

	return (
		<StyledSwitch
			title={brukt ? 'Marker som ikke i bruk' : 'Marker som i bruk'}
			checked={brukt}
			onChange={handleOnChange}
			onClick={(e: SyntheticEvent) => e.stopPropagation()}
			isSwitch
			{...props}
		/>
	)
}
