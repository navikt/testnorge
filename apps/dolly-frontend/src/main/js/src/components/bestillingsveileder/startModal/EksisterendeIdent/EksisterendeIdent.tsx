import React, { BaseSyntheticEvent, useState } from 'react'
import { useAsyncFn } from 'react-use'
import _get from 'lodash/get'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import { PdlforvalterApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'
import Icon from '~/components/ui/icon/Icon'

import './eksisterendeIdent.less'
import { Alert, TextField } from '@navikt/ds-react'

export const EksisterendeIdent = ({
	onAvbryt,
	onSubmit,
}: {
	onSubmit: (arg0: { opprettFraIdenter: any }) => any
	onAvbryt: () => void
}) => {
	const [text, setText] = useState('')
	const [state, fetch] = useAsyncFn(async () => {
		const identListe = text?.trim().split(/[\W\s]+/)
		const { data } = await PdlforvalterApi.getEksistens(identListe)
		return data
	}, [text])

	const _onSubmit = () =>
		onSubmit({
			opprettFraIdenter: state.value
				.filter((v: { available: any }) => v.available)
				.map((v: { ident: any }) => v.ident),
		})

	const statuser = _get(state, 'value', [])
	const finnesUgyldige = statuser.some((v) => !v.available)
	const finnesGyldige = statuser.some((v) => v.available)

	return (
		<div className="eksisterende-ident-form">
			{state.loading && <Loading />}

			{!state.value && !state.loading && (
				<React.Fragment>
					<TextField
						size={'small'}
						label="Identer"
						placeholder="fnr/dnr/npid"
						value={text}
						onChange={(e: BaseSyntheticEvent) => setText(e.target?.value)}
					/>

					<Alert variant="info">
						Skriv inn fnr/dnr/npid. Disse personene kan ikke eksistere i prod, eller finnes i Dolly
						fra før.
					</Alert>
					<NavButton variant={'primary'} onClick={() => fetch()} disabled={!text.length}>
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
						<Alert variant="warning">
							Det finnes personer markert som ikke gyldig. Kun gyldige personer blir tatt med.
						</Alert>
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
