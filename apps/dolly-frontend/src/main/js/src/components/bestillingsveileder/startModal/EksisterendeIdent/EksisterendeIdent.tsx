import React, { useContext, useEffect, useState } from 'react'
import { useFormContext } from 'react-hook-form'
import { Alert, Button, Table, Textarea } from '@navikt/ds-react'
import { usePdlfEksistens } from '@/utils/hooks/usePdlForvalter'
import Icon from '@/components/ui/icon/Icon'
import './eksisterendeIdent.less'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { BVOptions } from '@/components/bestillingsveileder/options/options'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'

export const EksisterendeIdent = ({ gruppeId }: any) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { dollyEnvironments } = useDollyEnvironments()

	const formMethods = useFormContext()

	const formEksisterendeIdenter = formMethods.watch('opprettFraIdenter')
	const [input, setInput] = useState(formEksisterendeIdenter?.join(','))
	const [submittedIds, setSubmittedIds] = useState(formEksisterendeIdenter)

	const parseIdentifiers = (text: string): string[] => {
		return (
			text
				?.trim()
				.split(/[\W\s]+/)
				.filter(Boolean) || []
		)
	}

	const { pdlfEksistens, loading, error } = usePdlfEksistens(submittedIds)

	useEffect(() => {
		const gyldigeIdenter = pdlfEksistens
			?.filter((status) => status.available)
			.map((status) => status.ident)
		opts.opprettFraIdenter = gyldigeIdenter
		if (!formEksisterendeIdenter || formEksisterendeIdenter?.length === 0) {
			const options = BVOptions(opts, gruppeId, dollyEnvironments)
			formMethods.reset(options.initialValues)
		}
		formMethods.setValue('opprettFraIdenter', gyldigeIdenter)
		formMethods.setValue('gruppeId', gruppeId)
	}, [pdlfEksistens])

	const hasInvalidIdentifiers = pdlfEksistens?.some((status) => !status.available)

	const onSubmit = () => {
		setSubmittedIds(parseIdentifiers(input))
	}

	const resetEksisterende = () => {
		setSubmittedIds(null)
		formMethods.setValue('opprettFraIdenter', null)
	}

	return (
		<div className="eksisterende-ident-form">
			{error && (
				<Alert variant="error">
					<Icon kind="advarsel" size="medium" />
					{error.message}
				</Alert>
			)}

			<Textarea
				onChange={(event) => setInput(event.target.value)}
				value={input}
				label="Identer"
				description={
					<>
						Skriv inn FNR/DNR/NPID adskilt med mellomrom, komma eller linjeskift.
						<br />
						Disse personene kan ikke eksistere i prod, eller finnes i Dolly fra før.
					</>
				}
				autoComplete="off"
			/>

			<div style={{ width: '-webkit-fill-available' }} className="form-actions">
				<Button
					type="submit"
					onClick={onSubmit}
					variant="primary"
					disabled={loading}
					loading={loading}
				>
					Valider identifikatorer
				</Button>
				{submittedIds && (
					<Button type="button" variant="secondary" onClick={resetEksisterende} disabled={loading}>
						Tøm
					</Button>
				)}
			</div>

			{pdlfEksistens?.length > 0 && (
				<Table size="medium" zebraStripes style={{ marginBottom: '20px' }}>
					<Table.Header>
						<Table.Row>
							<Table.HeaderCell scope="col">Ident</Table.HeaderCell>
							<Table.HeaderCell scope="col">Status</Table.HeaderCell>
							<Table.HeaderCell scope="col">OK</Table.HeaderCell>
						</Table.Row>
					</Table.Header>
					<Table.Body>
						{pdlfEksistens?.map(({ ident, status, available }, idx) => {
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
			)}

			{hasInvalidIdentifiers && (
				<Alert variant="warning">
					Det finnes personer markert som ikke gyldig. Kun gyldige personer blir tatt med.
				</Alert>
			)}
		</div>
	)
}
