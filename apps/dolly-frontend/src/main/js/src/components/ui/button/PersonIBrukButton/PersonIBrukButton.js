import React from 'react'
import { DollyCheckbox } from '../../form/inputs/checbox/Checkbox'

export const PersonIBrukButton = ({ ident, iLaastGruppe, updateIdentIbruk }) => {
	const handleOnChange = (event) => {
		updateIdentIbruk(ident.ident, !ident.ibruk)
	}
	return (
		<DollyCheckbox
			title={
				iLaastGruppe
					? 'Gruppen er låst og kan ikke endres'
					: ident.ibruk
					? 'Marker som ikke i bruk'
					: 'Marker som i bruk'
			}
			checked={ident.ibruk}
			onChange={handleOnChange}
			onClick={(e) => e.stopPropagation()}
			disabled={iLaastGruppe}
			isSwitch
		/>
	)
}
