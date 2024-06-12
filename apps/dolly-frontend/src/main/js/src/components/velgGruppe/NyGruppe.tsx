import React, { useState } from 'react'
import { DollyApi } from '@/service/Api'
import styled from 'styled-components'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { TestComponentSelectors } from '#/mocks/Selectors'

const FeedbackText = styled.div`
	margin-top: 10px;
`

export default () => {
	const [nyGruppe, setNyGruppe] = useState('')
	const [feilmelding, setFeilmelding] = useState('')
	const [navn, setNavn] = useState('')
	const [hensikt, setHensikt] = useState('')

	const onHandleSubmit = () => {
		if (navn.length === 0 || hensikt.length === 0) {
			setFeilmelding('Navn og hensikt må fylles ut')
			return
		}
		if (navn.length > 30 || hensikt.length > 200) {
			setFeilmelding('Navn kan maks være 30 bokstaver og hensikt kan maks være 200 bokstaver')
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
			setNyGruppe(`${data.id} - ${data.navn}`)
		})
	}
	return (
		<div className={'ny-gruppe'}>
			<div className="flexbox--flex-wrap">
				<DollyTextInput
					data-testid={TestComponentSelectors.INPUT_NY_GRUPPE_NAVN}
					onChange={(event) => setNavn(event.target.value)}
					label="NAVN"
					size="large"
				/>
				<DollyTextInput
					data-testid={TestComponentSelectors.INPUT_NY_GRUPPE_HENSIKT}
					onChange={(event) => setHensikt(event.target.value)}
					label="HENSIKT"
					size="large"
				/>
			</div>
			<NavButton
				data-testid={TestComponentSelectors.BUTTON_NY_GRUPPE_OPPRETT}
				variant="primary"
				onClick={(event) => {
					event.preventDefault()
					return onHandleSubmit()
				}}
			>
				Opprett
			</NavButton>
			<FeedbackText>
				{nyGruppe.length > 0 && <div>Gruppe ble opprettet: {nyGruppe}</div>}
				{feilmelding && <div className="error-message">{feilmelding}</div>}
			</FeedbackText>
		</div>
	)
}
