import React from 'react'
import './ManglerTilgang.less'
import Icon from '~/components/ui/icon/Icon'

export default () => {
	return (
		<div className={'mangler-tilgang-tekst'}>
			<Icon size={34} kind="report-problem-triangle" />
			<p>Du mangler tilgang til denne gruppen.</p>
		</div>
	)
}
