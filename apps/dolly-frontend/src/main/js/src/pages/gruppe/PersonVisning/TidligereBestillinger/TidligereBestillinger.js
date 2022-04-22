import React from 'react'

import './TidligereBestillinger.less'
import Button from '~/components/ui/button/Button'
import styled from 'styled-components'

export const NAVIGER_BESTILLING_ID = 'NAVIGER_BESTILLING_ID'
export const SEARCH_IDENT = 'SEARCH_IDENT'

const NavigerTilBestillingButton = styled(Button)`
	align-self: center;
`

export const TidligereBestillinger = ({ ids, setVisning, ident }) => {
	if (ids.length <= 1) return false

	return (
		<div className="tidligere-bestilling-panel">
			<h4>Bestilling-IDer</h4>
			{ids
				.map((numId) => numId.toString())
				.map((id) => (
					<NavigerTilBestillingButton
						key={id}
						onClick={() => {
							setVisning('bestilling')
							sessionStorage.setItem(NAVIGER_BESTILLING_ID, id)
							sessionStorage.setItem(SEARCH_IDENT, ident)
						}}
					>
						{id}
					</NavigerTilBestillingButton>
				))}
		</div>
	)
}
