import React from 'react'
import Icon from '~/components/ui/icon/Icon'

import './appError.less'

export const AppError = ({ title, message, error }) => (
	<div className="application-error">
		<h1>
			<Icon kind="report-problem-triangle" />
			{title || 'Ooops, dette var ikke planlagt...'}
		</h1>
		<h2>Feilmelding</h2>
		<pre>{message || 'Det har skjedd en feil og vi er ikke helt sikre på hvorfor. '}</pre>

		{error && (
			<React.Fragment>
				<h2>Error</h2>
				<pre>{JSON.stringify(error, null, 2)}</pre>
			</React.Fragment>
		)}

		<h2>Kontakt</h2>
		<p>Dersom en refresh ikke hjelper, ta kontakt med Dolly-teamet i kanalen #dolly på Slack.</p>
	</div>
)
