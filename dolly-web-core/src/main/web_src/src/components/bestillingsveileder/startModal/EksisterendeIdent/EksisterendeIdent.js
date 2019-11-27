import React, { useState } from 'react'
import { useAsyncFn } from 'react-use'
import { Textarea } from 'nav-frontend-skjema'
import _get from 'lodash/get'
import Alertstripe from 'nav-frontend-alertstriper'
import Knapp from 'nav-frontend-knapper'
import { TpsfApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import { ModalActions } from '../ModalActions'
import Icon from '~/components/ui/icon/Icon'

import './eksisterendeIdent.less'

export const EksisterendeIdent = ({ onAvbryt, onSubmit }) => {
	const [text, setText] = useState('')
	const [state, fetch] = useAsyncFn(async () => {
		const identListe = text.trim().split(/[\W\s]+/)
		const { data } = await TpsfApi.checkpersoner(identListe)
		return data.statuser
	}, [text])

	const _onSubmit = () =>
		onSubmit({ opprettFraIdenter: state.value.filter(v => v.available).map(v => v.ident) })

	const statuser = _get(state, 'value', [])
	const finnesUgyldige = statuser.some(v => !v.available)
	const finnesGyldige = statuser.some(v => v.available)

	return (
		<div className="eksisterende-ident-form">
			{state.loading && <Loading />}

			{!state.value && !state.loading && (
				<React.Fragment>
					<Textarea
						label="Testidenter"
						placeholder="fnr/dnr/bost"
						value={text}
						onChange={e => setText(e.target.value)}
					/>

					<Alertstripe type="info">
						Skriv inn fnr/dnr/bost. Disse identene kan ikke eksistere i prod, eller finnes i Dolly
						fra før.
					</Alertstripe>
					<Knapp type="hoved" onClick={() => fetch()} disabled={!text.length}>
						Hent testidenter
					</Knapp>
				</React.Fragment>
			)}

			{state.value && (
				<React.Fragment>
					<table className="tabell tabell--stripet" style={{ marginBottom: '20px' }}>
						<thead>
							<tr>
								<th>Ident</th>
								<th>Status</th>
								<th>OK</th>
							</tr>
						</thead>
						<tbody>
							{state.value.map((v, idx) => (
								<tr key={idx}>
									<td>{v.ident}</td>
									<td>{v.status}</td>
									<td>
										<Icon kind={v.available ? 'feedback-check-circle' : 'report-problem-circle'} />
									</td>
								</tr>
							))}
						</tbody>
					</table>

					{finnesUgyldige && (
						<Alertstripe type="advarsel">
							Det finnes ugyldige identer. Kun gyldige identer blir tatt med.
						</Alertstripe>
					)}
				</React.Fragment>
			)}

			{finnesGyldige && <ModalActions onAvbryt={onAvbryt} onSubmit={_onSubmit} />}
		</div>
	)
}
