import React, { useContext, useEffect, useState } from 'react'
import { useFormContext } from 'react-hook-form'
import { Alert, Button, Table, Textarea, VStack } from '@navikt/ds-react'
import { usePdlfEksistens } from '@/utils/hooks/usePdlForvalter'
import Icon from '@/components/ui/icon/Icon'
import './eksisterendeIdent.less'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

export const EksisterendeIdent = ({ gruppeId }: any) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
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

	const { pdlfEksistens, loading, error } = usePdlfEksistens(submittedIds as string[] | null)

	useEffect(() => {
		if (!pdlfEksistens) return
		const gyldigeIdenter = pdlfEksistens
			.filter((status: { available: boolean }) => status.available)
			.map((status: { ident: string }) => status.ident)
		opts.updateContext &&
			opts.updateContext({
				antall: gyldigeIdenter.length > 0 ? gyldigeIdenter.length : null,
				opprettFraIdenter: gyldigeIdenter,
				is: { ...opts.is, opprettFraIdenter: true },
			})
		formMethods.setValue('opprettFraIdenter', gyldigeIdenter)
		formMethods.setValue('gruppeId', gruppeId)
	}, [pdlfEksistens, gruppeId])

	const hasInvalidIdentifiers = pdlfEksistens?.some(
		(status: { available: boolean }) => !status.available,
	)

	const onSubmit = () => {
		setSubmittedIds(parseIdentifiers(input))
	}

	const resetEksisterende = () => {
		setSubmittedIds(null)
		setInput('')
		formMethods.setValue('opprettFraIdenter', [])
		opts.updateContext &&
			opts.updateContext({
				antall: null,
				opprettFraIdenter: [],
				is: { ...opts.is, opprettFraIdenter: true },
			})
	}

	return (
		<div className="eksisterende-ident-form">
			{error && (
				<Alert variant="error">
					<Icon kind="report-problem-triangle" />
					{error.message}
				</Alert>
			)}
			<VStack gap="2" style={{ maxHeight: '20vh', marginRight: '20px' }}>
				<Textarea
					UNSAFE_autoScrollbar
					onChange={(event) => setInput(event.target.value)}
					value={input}
					label="Identer"
					description={
						<>
							Skriv inn FNR/DNR/NPID adskilt med mellomrom, komma eller linjeskift. Disse personene
							kan ikke eksistere i prod, eller finnes i Dolly fra før.
						</>
					}
					autoComplete="off"
				/>
			</VStack>
			<div style={{ width: '-webkit-fill-available' }} className="form-actions">
				<Button
					style={{ marginRight: '10px' }}
					type="submit"
					onClick={onSubmit}
					variant="primary"
					disabled={!input || loading}
					loading={loading}
				>
					Valider identifikatorer
				</Button>
			</div>
			{pdlfEksistens?.length > 0 && (
				<div>
					<Table size="medium" zebraStripes style={{ marginBottom: '20px', marginRight: '20px' }}>
						<Table.Header>
							<Table.Row>
								<Table.HeaderCell scope="col">Ident</Table.HeaderCell>
								<Table.HeaderCell scope="col">Status</Table.HeaderCell>
								<Table.HeaderCell scope="col">OK</Table.HeaderCell>
							</Table.Row>
						</Table.Header>
						<Table.Body>
							{pdlfEksistens?.map(
								(
									{
										ident,
										status,
										available,
									}: { ident: string; status: string; available: boolean },
									idx: number,
								) => {
									return (
										<Table.Row key={idx}>
											<Table.HeaderCell scope="row">{ident}</Table.HeaderCell>
											<Table.HeaderCell>{status}</Table.HeaderCell>
											<Table.HeaderCell>
												<Icon
													kind={available ? 'feedback-check-circle' : 'report-problem-circle'}
												/>
											</Table.HeaderCell>
										</Table.Row>
									)
								},
							)}
						</Table.Body>
					</Table>

					<Button
						style={{ marginBottom: '20px' }}
						type="button"
						variant="secondary"
						onClick={resetEksisterende}
						disabled={loading}
					>
						Tøm
					</Button>
				</div>
			)}
			{hasInvalidIdentifiers && (
				<Alert variant="warning" style={{ width: '100%', marginRight: '20px' }}>
					Det finnes personer markert som ikke gyldig. Kun gyldige personer blir tatt med.
				</Alert>
			)}
		</div>
	)
}
