import React, { useState } from 'react'
import { useAsyncFn } from 'react-use'
import { Textarea } from 'nav-frontend-skjema'
import _get from 'lodash/get'
import Alertstripe from 'nav-frontend-alertstriper'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { PdlforvalterApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'
import Icon from '~/components/ui/icon/Icon'

import './eksisterendeIdent.less'

export const EksisterendeIdent = ({ onAvbryt, onSubmit }) => {
	const [text, setText] = useState('')
	const [state, fetch] = useAsyncFn(async () => {
		const identListe = text.trim().split(/[\W\s]+/)
		const { data } = await PdlforvalterApi.getEksistens(identListe)
		return data
	}, [text])

	const _onSubmit = () =>
		onSubmit({ opprettFraIdenter: state.value.filter((v) => v.available).map((v) => v.ident) })

	const statuser = _get(state, 'value', [])
	const finnesUgyldige = statuser.some((v) => !v.available)
	const finnesGyldige = statuser.some((v) => v.available)

	return (
		<div className="eksisterende-ident-form">
			{state.loading && <Loading />}

			{!state.value && !state.loading && (
				<React.Fragment>
					<Textarea
						label="Identer"
						placeholder="fnr/dnr/npid"
						value={text}
						onChange={(e) => setText(e.target.value)}
					/>

					<Alertstripe type="info">
						Skriv inn fnr/dnr/npid. Disse personene kan ikke eksistere i prod, eller finnes i Dolly
						fra før.
					</Alertstripe>
					<NavButton type="hoved" onClick={() => fetch()} disabled={!text.length}>
						Sjekk gyldige personer
					</NavButton>
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
							Det finnes personer markert som ikke gyldig. Kun gyldige personer blir tatt med.
						</Alertstripe>
					)}
				</React.Fragment>
			)}

			{finnesGyldige && (
				<ModalActionKnapper
					submitknapp="Start bestilling"
					onSubmit={_onSubmit}
					onAvbryt={onAvbryt}
					center
				/>
			)}
		</div>
	)
}
