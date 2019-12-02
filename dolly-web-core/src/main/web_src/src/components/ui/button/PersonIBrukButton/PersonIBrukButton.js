import React from 'react'
import Button from '../Button'

export const PersonIBrukButton = ({ ident, updateIdentIbruk }) => {
	return (
		<Button
			className="flexbox--align-center"
			title={ident.ibruk ? 'Marker som ikke i bruk' : 'Marker som i bruk'}
			kind={ident.ibruk ? 'line-version-expanded-button-empty' : 'filled-version-button-empty'}
			onClick={() => updateIdentIbruk(ident.ident, !ident.ibruk)}
		/>
	)
}
