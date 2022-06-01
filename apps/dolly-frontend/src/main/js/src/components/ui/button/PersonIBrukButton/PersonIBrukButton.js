import React, { useState } from 'react'
import { DollyCheckbox } from '../../form/inputs/checbox/Checkbox'

export const PersonIBrukButton = ({ ident, updateIdentIbruk }) => {
	const [brukt, setBrukt] = useState(ident.ibruk)
	const handleOnChange = (event) => {
		const erIBruk = event.target.checked
		setBrukt(erIBruk)
		updateIdentIbruk(ident.ident, erIBruk)
	}
	return (
		<DollyCheckbox
			title={brukt ? 'Marker som ikke i bruk' : 'Marker som i bruk'}
			checked={brukt}
			onChange={handleOnChange}
			onClick={(e) => e.stopPropagation()}
			isSwitch
		/>
	)
}
