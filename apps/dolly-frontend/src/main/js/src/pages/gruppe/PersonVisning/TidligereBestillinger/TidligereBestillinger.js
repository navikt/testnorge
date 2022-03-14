import React from 'react'

import './TidligereBestillinger.less'
import Button from '~/components/ui/button/Button'
import styled from 'styled-components'

export const NAVIGER_BESTILLING_ID = 'NAVIGER_BESTILLING_ID'

export const TidligereBestillinger = ({ ids }) => {
	if (ids.length <= 1) return false
	return (
		<div className="tidligere-bestilling-panel">
			<h4>Tidligere bestilling-ID</h4>
			<div>{ids.slice(1).join(', ')}</div>
		</div>
	)
}
