import React from 'react'
import Icon from '~/components/ui/icon/Icon'

import './appError.less'
import { CopyToClipboard } from 'react-copy-to-clipboard'
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap.css'

export const AppError = ({ error, stackTrace, style }) => (
	<div className="application-error" style={style}>
		<h1>
			<Icon kind="report-problem-triangle" />
			{'Ooops, dette var ikke planlagt...'}
		</h1>
		{error &&
		error.toString().includes('dolly config') &&
		window.location.href.includes('localhost') ? (
			<div>
				<h2>Lokal Utvikling</h2>
				<pre>
					Det ser ut til at du utvikler lokalt, forsøk å logge inn på:{' '}
					<a
						target={'_blank'}
						href="https://isso-t.adeo.no/isso/oauth2/authorize?client_id=dolly-local-localhost&state=821ad368b1824bff9562a5359d218e56&nonce=014999e5-58f8-49c9-b072-697c7b202063&response_type=code&scope=openid&redirect_uri=http://localhost:8020/dolly/login"
					>
						Dolly T2
					</a>
				</pre>
			</div>
		) : null}
		<h2>Feilmelding</h2>
		<pre>
			{error ? error.toString() : 'Det har skjedd en feil og vi er ikke helt sikre på hvorfor. '}
		</pre>
		{error && (
			<div className={'flexbox--align-start flexbox--wrap'}>
				<CopyToClipboard text={error.toString() + '\n' + stackTrace}>
					<Tooltip overlay={'Kopier'} placement={'top'} mouseEnterDelay={0} mouseLeaveDelay={0.1}>
						<div
							className="icon"
							style={{ paddingTop: '8px' }}
							onClick={event => {
								event.stopPropagation()
							}}
						>
							<Icon kind="copy" size={20} />
						</div>
					</Tooltip>
				</CopyToClipboard>

				<details style={{ whiteSpace: 'pre-wrap', padding: '10px' }}>
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

		<h2>Kontakt</h2>
		<p>
			Noe gikk galt under visning av elementet. <br />
			Vennligst kontakt Team Dolly på slack kanalen{' '}
			<b>
				<a href="https://nav-it.slack.com/archives/CA3P9NGA2">#dolly</a>
			</b>{' '}
			<br />
			med detaljene over (bruk kopier ikonet) dersom problemet vedvarer etter en refresh.
		</p>
	</div>
)
