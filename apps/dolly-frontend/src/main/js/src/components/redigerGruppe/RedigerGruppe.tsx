import { useNavigate } from 'react-router'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import * as Yup from 'yup'
import { DollyApi } from '@/service/Api'
import React, { useState } from 'react'
import Loading from '@/components/ui/loading/Loading'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import './RedigerGruppe.less'
import { Alert } from '@navikt/ds-react'

type Props = {
	gruppeId: string
	onCancel: Function
}

const validation = Yup.object().shape({
	navn: Yup.string().trim().required('Gi gruppen et navn').max(30, 'Maksimalt 30 bokstaver'),
	hensikt: Yup.string()
		.trim()
		.required('Beskriv hensikten med gruppen')
		.max(200, 'Maksimalt 200 bokstaver'),
})

export const RedigerGruppe = ({ gruppeId, onCancel }: Props) => {
	'use no memo' // Skip compilation for this component
	const navigate = useNavigate()
	const { gruppe } = useGruppeById(gruppeId)
	const erRedigering = gruppeId !== undefined

	const [feilmelding, setFeilmelding] = useState('')
	const [isLoading, setIsLoading] = useState(false)

	const initialValues = {
		navn: gruppe?.navn || '',
		hensikt: gruppe?.hensikt || '',
	}

	const mutate = useMatchMutate()
	const formMethods = useForm({
		mode: 'onChange',
		defaultValues: initialValues,
		resolver: yupResolver(validation),
	})

	const handleCreateGruppe = () => {
		setIsLoading(true)
		DollyApi.createGruppe({
			navn: formMethods.watch('navn'),
			hensikt: formMethods.watch('hensikt'),
		}).then((response: any) => {
			if (response.error) {
				setFeilmelding('Noe gikk galt under oppretting av gruppe: ' + response.error)
				setIsLoading(false)
			} else {
				setFeilmelding('')
				setIsLoading(false)
				const gruppeId = response.data?.id
				if (gruppeId) {
					navigate(`/gruppe/${gruppeId}`)
				}
			}
		})
	}

	const handleUpdateGruppe = () => {
		setIsLoading(true)
		DollyApi.updateGruppe(gruppeId, {
			navn: formMethods.watch('navn'),
			hensikt: formMethods.watch('hensikt'),
		}).then((response: any) => {
			if (response.error) {
				setFeilmelding('Noe gikk galt under redigering av gruppe: ' + response.error)
				setIsLoading(false)
			} else {
				setFeilmelding('')
				setIsLoading(false)
				onCancel()
				return mutate(REGEX_BACKEND_GRUPPER)
			}
		})
	}

	if (isLoading) {
		return (
			<div style={{ marginBottom: '15px' }}>
				<Loading label="Oppdaterer gruppe ..." />
			</div>
		)
	}

	const buttons = (
		<>
			<NavButton
				data-testid={TestComponentSelectors.BUTTON_OPPRETT}
				variant={'primary'}
				type={'submit'}
			>
				{erRedigering ? 'Lagre' : 'Opprett og gå til gruppe'}
			</NavButton>
			<NavButton type={'reset'} variant={'danger'} onClick={() => onCancel()}>
				Avbryt
			</NavButton>
		</>
	)

	return (
		<FormProvider {...formMethods}>
			<form
				className={'opprett-tabellrad'}
				autoComplete={'off'}
				onSubmit={formMethods.handleSubmit(erRedigering ? handleUpdateGruppe : handleCreateGruppe)}
			>
				<div className="fields">
					<DollyTextInput
						data-testid={TestComponentSelectors.INPUT_NAVN}
						name="navn"
						label="NAVN"
						size="grow"
						autoFocus
					/>
					<DollyTextInput
						data-testid={TestComponentSelectors.INPUT_HENSIKT}
						name="hensikt"
						label="HENSIKT"
						size="grow"
					/>
					{buttons}
				</div>
				{feilmelding && (
					<Alert variant={'error'} size={'small'} style={{ margin: '0 10px 15px' }}>
						{feilmelding}
					</Alert>
				)}
			</form>
		</FormProvider>
	)
}
