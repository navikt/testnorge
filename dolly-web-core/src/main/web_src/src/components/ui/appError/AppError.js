import React from 'react'
import Icon from '~/components/ui/icon/Icon'

import './appError.less'

export const AppError = ({ error }) => (
	<div className="application-error">
		<h1>
			<Icon kind="report-problem-triangle" />
			Ooops, dette var ikke planlagt...
		</h1>
		<h2>Error</h2>
		<pre>{error || 'Det har skjedd en feil og vi er ikke helt sikre på hvorfor. '}</pre>

		<h2>Kontakt</h2>
		<p>Dersom en refresh ikke hjelper, ta kontakt med Dolly-teamet i kanalen #dolly på Slack.</p>
	</div>
)
