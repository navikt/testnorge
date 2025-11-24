import React, { useState } from 'react'
import { Alert } from '@navikt/ds-react'
import { Close } from '@navikt/ds-icons'

import './DollyInfoAlert.less'

type DollyInfoAlertType = {
	id: number
	text: string
	type: 'error' | 'warning' | 'info' | 'success' | any
	onHide: Function
}

export const DollyInfoAlert = ({ type, text, id, onHide }: DollyInfoAlertType) => {
	const [hide, setHide] = useState(false)
	if (hide) {
		return null
	}
	return (
		<div className="dolly-info-alert">
			<Alert size={'small'} variant={type?.toLowerCase()} style={{ width: '100%' }}>
				{text}
			</Alert>
			<span>
				<Close
					title="Lukk denne meldingen"
					onClick={() => {
						setHide(true)
						onHide(id)
					}}
				/>
			</span>
		</div>
	)
}
