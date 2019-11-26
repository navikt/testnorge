import React from 'react'
import Formatters from '~/utils/DataFormatter'

import './TidligereBestillinger.less'

export const TidligereBestillinger = ({ ids }) => {
	if (ids.length <= 1) return false
	return (
		<div className="tidligere-bestilling-panel">
			<h4>Tidligere bestilling-ID</h4>
			<div>{Formatters.arrayToString(ids)}</div>
		</div>
	)
}
