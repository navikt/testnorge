import React from 'react'

import './TidligereBestillinger.less'
import Button from '~/components/ui/button/Button'
import styled from 'styled-components'
import { useDispatch } from 'react-redux'
import { navigerTilBestilling } from '~/ducks/finnPerson'

const NavigerTilBestillingButton = styled(Button)`
	align-self: center;
`

export const TidligereBestillinger = ({ ids }) => {
	if (ids.length <= 1) return false

	const dispatch = useDispatch()

	return (
		<div className="tidligere-bestilling-panel">
			<h4>Bestilling-IDer</h4>
			{ids
				.map((numId) => numId.toString())
				.map((id) => (
					<NavigerTilBestillingButton
						key={id}
						onClick={() => {
							dispatch(navigerTilBestilling(id))
						}}
					>
						{id}
					</NavigerTilBestillingButton>
				))}
		</div>
	)
}
