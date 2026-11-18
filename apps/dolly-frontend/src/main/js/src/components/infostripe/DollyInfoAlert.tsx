import React, { useState } from 'react'
import { Button, InfoCard } from '@navikt/ds-react'
import './DollyInfoAlert.less'
import { XMarkIcon } from '@navikt/aksel-icons'
import { getInfostripeColor, InfostripeIcon } from '@/components/infostripe/InfostripeIcon'

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
			<InfoCard size={'small'} data-color={getInfostripeColor(type)} style={{ width: '100%' }}>
				<InfoCard.Message icon={<InfostripeIcon type={type} />}>
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
				</InfoCard.Message>
			</InfoCard>
		</div>
	)
}
