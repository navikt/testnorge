import React from 'react'

import './TidligereBestillinger.less'

export const TidligereBestillinger = ({ ids }) => {
	if (ids.length <= 1) return false
	return (
		<div className="tidligere-bestilling-panel">
			<h4>Tidligere bestilling-ID</h4>
			<div>{ids.slice(1).join(', ')}</div>
		</div>
	)
}