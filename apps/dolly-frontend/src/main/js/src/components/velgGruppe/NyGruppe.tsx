import React, { useContext, useEffect, useState } from 'react'
import { DollyApi } from '@/service/Api'
import styled from 'styled-components'
import { useFormContext } from 'react-hook-form'
import { TestComponentSelectors } from '#/mocks/Selectors'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import { Box, Button, HStack, TextField } from '@navikt/ds-react'

const FeedbackText = styled.div`
	margin-top: 10px;
`

export default () => {
	const [nyGruppe, setNyGruppe] = useState('')
	const [gruppeId, setGruppeId] = useState('')
	const [feilmelding, setFeilmelding] = useState('')

	const formMethods = useFormContext()
	const formErrors = formMethods.formState.errors
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)

	const navn = formMethods.watch('gruppeNavn') || ''
	const hensikt = formMethods.watch('gruppeHensikt') || ''
	const [navnError, setNavnError] = useState(null as unknown as string)
	const [hensiktError, setHensiktError] = useState(null as unknown as string)

	useEffect(() => {
		formMethods.setValue('gruppeId', gruppeId)
	}, [gruppeId])

	const onHandleSubmit = () => {
		if (navn.length === 0) {
			setNavnError('Navn må fylles ut')
		} else if (navn.length > 30) {
			setNavnError('Navn kan maks være 30 bokstaver')
		}
		if (hensikt.length === 0) {
			setHensiktError('Hensikt må fylles ut')
		} else if (hensikt.length > 200) {
			setHensiktError('Hensikt kan maks være 200 bokstaver')
		}
		if (navn.length === 0 || navn.length > 30 || hensikt.length === 0 || hensikt.length > 200) {
			return
		}

		DollyApi.createGruppe({
			navn: navn,
			hensikt: hensikt,
		}).then((response: any) => {
			if (response.error) {
				setFeilmelding('Noe gikk galt under oppretting av gruppe: ' + response.error)
			} else {
				setFeilmelding('')
			}
			const { data } = response
			if (data) {
				setGruppeId(data?.id)
				setNyGruppe(`${data?.id} - ${data?.navn}`)
				formMethods.clearErrors('gruppeId')
			} else {
				setGruppeId('')
				setNyGruppe('')
			}
		})
	}

	return (
		<div className={'ny-gruppe'}>
			<HStack gap="space-16" width="100%" wrap={false}>
				<Box flexGrow="1" flexBasis="0">
					<TextField
						name={'gruppeNavn'}
						data-testid={TestComponentSelectors.INPUT_NY_GRUPPE_NAVN}
						onChange={(event) => {
							formMethods.setValue('gruppeNavn', event.target.value)
							setNavnError(null as unknown as string)
						}}
						value={navn}
						label="Navn"
						error={navnError}
					/>
				</Box>
				<Box flexGrow="1" flexBasis="0">
					<TextField
						name={'gruppeHensikt'}
						data-testid={TestComponentSelectors.INPUT_NY_GRUPPE_HENSIKT}
						onChange={(event) => {
							formMethods.setValue('gruppeHensikt', event.target.value)
							setHensiktError(null as unknown as string)
						}}
						value={hensikt}
						label="Hensikt"
						error={hensiktError}
					/>
				</Box>
				<Button
					data-testid={TestComponentSelectors.BUTTON_NY_GRUPPE_OPPRETT}
					variant="primary"
					disabled={nyGruppe.length > 0}
					onClick={(event) => {
						event.preventDefault()
						return onHandleSubmit()
					}}
					style={{ height: 'fit-content', marginTop: '32px' }}
				>
					Opprett
				</Button>
			</HStack>
			<FeedbackText>
				{nyGruppe.length > 0 && <div>Gruppe ble opprettet: {nyGruppe}</div>}
				{feilmelding && <div className="error-message">{feilmelding}</div>}
				{formErrors?.gruppeId && errorContext && (
					<div className="error-message">{'Opprett en gruppe med navn og hensikt'}</div>
				)}
			</FeedbackText>
		</div>
	)
}
