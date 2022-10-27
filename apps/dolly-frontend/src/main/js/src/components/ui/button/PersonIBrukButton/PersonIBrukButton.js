import React, { useState } from 'react'
import { DollyCheckbox } from '../../form/inputs/checbox/Checkbox'
import styled from 'styled-components'

export const PersonIBrukButton = ({ ident, updateIdentIbruk }) => {
	const [brukt, setBrukt] = useState(ident.ibruk)
	const handleOnChange = (event) => {
		const erIBruk = event.target.checked
		setBrukt(erIBruk)
		updateIdentIbruk(ident.ident, erIBruk)
	}

	const StyledSwitch = styled(DollyCheckbox)`
		.navds-switch__input:checked ~ .navds-switch__track {
			background-color: #0067c5;
		}

		.navds-switch__input:checked ~ .navds-switch__track > .navds-switch__thumb {
			color: #0067c5;
		}
	`

	return (
		<StyledSwitch
			title={brukt ? 'Marker som ikke i bruk' : 'Marker som i bruk'}
			checked={brukt}
			onChange={handleOnChange}
			onClick={(e) => e.stopPropagation()}
			isSwitch
		/>
	)
}
