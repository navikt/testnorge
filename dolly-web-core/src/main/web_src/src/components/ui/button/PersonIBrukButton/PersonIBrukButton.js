import React from 'react'
import Button from '../Button'

export const PersonIBrukButton = ({ ident, updateIdentIbruk }) => {
	return (
		<Button
			title={ident.ibruk ? 'Marker som ikke i bruk' : 'Marker som i bruk'}
			kind={ident.ibruk ? 'rectangle-filled' : 'rectangle-empty'}
			onClick={() => updateIdentIbruk(ident.ident, !ident.ibruk)}
		/>
	)
}
