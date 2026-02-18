import React, { useState } from 'react'
import { Alert, Button } from '@navikt/ds-react'
import './DollyInfoAlert.less'
import { XMarkIcon } from '@navikt/aksel-icons'

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
				<Button
					variant="tertiary"
					size="small"
					icon={<XMarkIcon title="Lukk" />}
					onClick={() => {
						setHide(true)
						onHide(id)
					}}
				/>
			</Alert>
		</div>
	)
}
