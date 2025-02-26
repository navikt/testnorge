import { useNavigate } from 'react-router'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'
import * as Yup from 'yup'
import { DollyApi } from '@/service/Api'
import React, { useState } from 'react'
import Loading from '@/components/ui/loading/Loading'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import './RedigerGruppe.less'

//TODO: props

const validation = Yup.object().shape({
	navn: Yup.string().trim().required('Gi gruppen et navn').max(30, 'Maksimalt 30 bokstaver'),
	hensikt: Yup.string()
		.trim()
		.required('Beskriv hensikten med gruppen')
		.max(200, 'Maksimalt 200 bokstaver'),
})

export const RedigerGruppeNy = ({ gruppeId, onCancel }: any) => {
	const navigate = useNavigate()
	const { gruppe } = useGruppeById(gruppeId)
	const erRedigering = gruppeId !== undefined

	const [feilmelding, setFeilmelding] = useState('') //TODO: Test feilmelding
	const [isLoading, setIsLoading] = useState(false)

	const initialValues = {
		navn: gruppe?.navn || '',
		hensikt: gruppe?.hensikt || '',
	}

	const mutate = useMatchMutate()
	const formMethods = useForm({
		mode: 'all',
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
			} else {
				setFeilmelding('')
				setIsLoading(false) //TODO: staa et annet sted?
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
				onClick={() => {
					formMethods.trigger(['navn', 'hensikt'])
					formMethods.setValue('navn', formMethods.getValues('navn'), {
						shouldValidate: true,
						shouldTouch: true,
					})
					formMethods.setValue('hensikt', formMethods.getValues('hensikt'), {
						shouldValidate: true,
						shouldTouch: true,
					})
				}}
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
			<Form
				control={formMethods.control}
				className={'opprett-tabellrad'}
				autoComplete={'off'}
				onSubmit={formMethods.handleSubmit(erRedigering ? handleUpdateGruppe : handleCreateGruppe)}
			>
				<div className="fields">
					<FormTextInput
						data-testid={TestComponentSelectors.INPUT_NAVN}
						name="navn"
						label="NAVN"
						size="grow"
						useOnChange={true}
						autoFocus
					/>
					<FormTextInput
						data-testid={TestComponentSelectors.INPUT_HENSIKT}
						name="hensikt"
						label="HENSIKT"
						size="grow"
						useOnChange={true}
					/>
					{buttons}
				</div>
				{feilmelding && (
					<div className="opprett-error">
						<span>{feilmelding}</span>
					</div>
				)}
			</Form>
		</FormProvider>
	)
}
