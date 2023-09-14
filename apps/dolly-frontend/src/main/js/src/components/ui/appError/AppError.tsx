import Icon from '@/components/ui/icon/Icon'

import './appError.less'
import 'rc-tooltip/assets/bootstrap.css'
import { DollyCopyButton } from '@/components/ui/button/CopyButton/DollyCopyButton'
import { CSSProperties } from 'react'

type Props = {
	style?: CSSProperties | undefined
	error: Error | string
	stackTrace: string
}
export const AppError = ({ error, stackTrace, style }: Props) => (
	<div className="application-error" style={style}>
		<h1>
			<Icon kind="report-problem-triangle" />
			{'Ooops, dette var ikke planlagt...'}
		</h1>
		<h2>Feilmelding</h2>
		<pre>
			{error ? error.toString() : 'Det har skjedd en feil og vi er ikke helt sikre på hvorfor. '}
		</pre>
		{error && (
			<div className={'flexbox--align-start flexbox--wrap'}>
				<DollyCopyButton
					copyText={error.toString() + '\n' + stackTrace}
					style={{ width: '100%' }}
					tooltipText={'Kopier feilmelding'}
				/>

				<details style={{ whiteSpace: 'pre-wrap', padding: '10px', cursor: 'pointer' }}>
					<summary>Detaljert Feilbeskrivelse</summary>
					<p>
						<br />
						{error && error.toString()}
						<br />
						{stackTrace}
					</p>
				</details>
			</div>
		)}

		<p>
			Noe gikk galt under visning av elementet. <br />
			Dersom refresh av siden ikke fungerer, forsøk å trykke{' '}
			<b>
				<a href={window.location.protocol + '//' + window.location.host + '/oauth2/logout'}>her</a>
			</b>
			, clear cookies eller kontakt Team Dolly på slack{' '}
			<b>
				<a href="https://nav-it.slack.com/archives/CA3P9NGA2">#dolly</a>
			</b>{' '}
			med detaljene over (bruk kopier-ikonet).
		</p>
	</div>
)
