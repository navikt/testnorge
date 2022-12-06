import React, { BaseSyntheticEvent, useState } from 'react'
import { useAsyncFn } from 'react-use'
import _get from 'lodash/get'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { PdlforvalterApi } from '@/service/Api'
import Loading from '@/components/ui/loading/Loading'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import Icon from '@/components/ui/icon/Icon'

import './eksisterendeIdent.less'
import { Alert, Table, Textarea } from '@navikt/ds-react'

export const EksisterendeIdent = ({
	onAvbryt,
	onSubmit,
}: {
	onSubmit: (arg0: any, arg1?: any) => any
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
					<Textarea
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
					<Table size="medium" zebraStripes style={{ marginBottom: '20px' }}>
						<Table.Header>
							<Table.Row>
								<Table.HeaderCell scope="col">Ident</Table.HeaderCell>
								<Table.HeaderCell scope="col">Status</Table.HeaderCell>
								<Table.HeaderCell scope="col">OK</Table.HeaderCell>
							</Table.Row>
						</Table.Header>
						<Table.Body>
							{state.value.map(({ ident, status, available }, idx) => {
								return (
									<Table.Row key={idx}>
										<Table.HeaderCell scope="row">{ident}</Table.HeaderCell>
										<Table.HeaderCell>{status}</Table.HeaderCell>
										<Table.HeaderCell>
											<Icon kind={available ? 'feedback-check-circle' : 'report-problem-circle'} />
										</Table.HeaderCell>
									</Table.Row>
								)
							})}
						</Table.Body>
					</Table>

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
