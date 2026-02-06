import React, { SyntheticEvent, useState } from 'react'
import { Switch } from '@navikt/ds-react'

export const PersonIBrukButton = ({ ident, updateIdentIbruk }) => {
	const [brukt, setBrukt] = useState(ident.ibruk)
	const handleOnChange = (event: any) => {
		const erIBruk = event.target.checked
		setBrukt(erIBruk)
		updateIdentIbruk(ident.ident, erIBruk)
	}

	return (
		<Switch
			checked={brukt}
			onChange={handleOnChange}
			onClick={(e: SyntheticEvent) => e.stopPropagation()}
			hideLabel
		>
			{brukt ? 'Marker som ikke i bruk' : 'Marker som i bruk'}
		</Switch>
	)
}
