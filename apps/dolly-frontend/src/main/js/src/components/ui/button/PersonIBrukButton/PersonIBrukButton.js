import React from 'react'
import { DollyCheckbox } from '../../form/inputs/checbox/Checkbox'

export const PersonIBrukButton = ({ ident, updateIdentIbruk }) => {
	const handleOnChange = (event) => {
		updateIdentIbruk(ident.ident, !ident.ibruk)
	}
	return (
		<DollyCheckbox
			title={ident.ibruk ? 'Marker som ikke i bruk' : 'Marker som i bruk'}
			checked={ident.ibruk}
			onChange={handleOnChange}
			onClick={(e) => e.stopPropagation()}
			isSwitch
		/>
	)
}
