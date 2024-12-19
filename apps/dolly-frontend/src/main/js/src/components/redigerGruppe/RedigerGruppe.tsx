import React from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import * as yup from 'yup'
import Loading from '@/components/ui/loading/Loading'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

import './RedigerGruppe.less'
import { useNavigate } from 'react-router-dom'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import * as _ from 'lodash-es'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { yupResolver } from '@hookform/resolvers/yup'

type Props = {
	gruppeId?: string
	createGruppe: (arg0: any) => any
	createOrUpdateFetching: boolean
	updateGruppe: (arg0: number, arg1: any) => any
	onCancel: () => void
	error: {
		message: string
	}
}

const validation = () =>
	yup.object().shape({
		navn: yup.string().trim().required('Navn er et påkrevd felt').max(30, 'Maksimalt 30 bokstaver'),
		hensikt: yup
			.string()
			.trim()
			.required('Gi en beskrivelse av hensikten med gruppen')
			.max(200, 'Maksimalt 200 bokstaver'),
	})
const RedigerGruppe = ({
	gruppeId,
	createGruppe,
	createOrUpdateFetching,
	updateGruppe,
	onCancel,
	error,
}: Props): JSX.Element => {
	const navigate = useNavigate()
	const { gruppe } = useGruppeById(gruppeId)
	const erRedigering = gruppe?.id !== undefined
	const initialValues = {
		navn: _.get(gruppe, 'navn', ''),
		hensikt: _.get(gruppe, 'hensikt', ''),
	}
	const mutate = useMatchMutate()
	const formMethods = useForm({
		mode: 'onBlur',
		defaultValues: initialValues,
		resolver: yupResolver(validation()),
	})

	const onHandleSubmit = async (
		values: {
			hensikt: any
			navn: any
		},
		_actions: any,
	) => {
		const groupValues = {
			hensikt: values.hensikt,
			navn: values.navn,
		}
		erRedigering
			? await updateGruppe(gruppe.id, groupValues).then(() => {
					return mutate(REGEX_BACKEND_GRUPPER)
				})
			: await createGruppe(groupValues).then(
					(response: {
						value: {
							data: {
								id: any
							}
						}
					}) => {
						const gruppeId = response.value?.data?.id
						if (gruppeId) {
							navigate(`/gruppe/${gruppeId}`)
						}
					},
				)
		return !error && onCancel()
	}

	if (createOrUpdateFetching) {
		return <Loading label="oppdaterer gruppe" />
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
			<Form
				control={formMethods.control}
				className={'opprett-tabellrad'}
				autoComplete={'off'}
				onSubmit={formMethods.handleSubmit(onHandleSubmit)}
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
				{error && (
					<div className="opprett-error">
						<span>{error.message}</span>
					</div>
				)}
			</Form>
		</FormProvider>
	)
}
export default RedigerGruppe
