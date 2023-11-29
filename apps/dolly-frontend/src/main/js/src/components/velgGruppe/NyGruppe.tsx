import React, { useState } from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import * as yup from 'yup'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { DollyApi } from '@/service/Api'
import styled from 'styled-components'
import { Form, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'

interface NyGruppe {
	setValgtGruppe: React.Dispatch<React.SetStateAction<string>>
}

const initialValues = {
	navn: '',
	hensikt: '',
}

const FeedbackText = styled.div`
	margin-top: 10px;
`

const validation = () =>
	yup.object().shape({
		navn: yup.string().trim().required('Navn er et påkrevd felt').max(30, 'Maksimalt 30 bokstaver'),
		hensikt: yup
			.string()
			.trim()
			.required('Gi en liten beskrivelse av hensikten med gruppen')
			.max(200, 'Maksimalt 200 bokstaver'),
	})

export default ({ setValgtGruppe }: NyGruppe) => {
	const [nyGruppe, setNyGruppe] = useState('')
	const [feilmelding, setFeilmelding] = useState('')
	const formMethods = useForm({ defaultValues: initialValues, resolver: yupResolver(validation()) })

	const onHandleSubmit = async (values: any) => {
		await DollyApi.createGruppe({
			navn: values.navn,
			hensikt: values.hensikt,
		}).then((response: any) => {
			if (response.error) {
				setFeilmelding('Noe gikk galt under oppretting av gruppe: ' + response.error)
			}
			const { data } = response
			setValgtGruppe(data.id)
			setNyGruppe(`${data.id} - ${data.navn}`)
		})
	}
	return (
		<>
			<Form
				className={'ny-gruppe'}
				autoComplete={'off'}
				onSubmit={() => formMethods.handleSubmit(onHandleSubmit)}
			>
				<div className="flexbox--flex-wrap">
					<FormikTextInput name="navn" label="NAVN" size="small" autoFocus />
					<FormikTextInput name="hensikt" label="HENSIKT" size="medium" />
				</div>
				<NavButton variant="primary" type="submit">
					Opprett
				</NavButton>
			</Form>
			<FeedbackText>
				{nyGruppe.length > 0 && <div>Gruppe ble opprettet: {nyGruppe}</div>}
				{feilmelding && <div className="error-message">{feilmelding}</div>}
			</FeedbackText>
		</>
	)
}
