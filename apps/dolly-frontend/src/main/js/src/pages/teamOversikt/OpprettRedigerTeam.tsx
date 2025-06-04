import NavButton from '@/components/ui/button/NavButton/NavButton'
import React, { useState } from 'react'
import { Form, FormProvider, useForm } from 'react-hook-form'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { DollyApi } from '@/service/Api'
import { useAlleBrukere, useCurrentBruker } from '@/utils/hooks/useBruker'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { REGEX_BACKEND_TEAM, useMatchMutate } from '@/utils/hooks/useMutate'
import * as Yup from 'yup'
import { yupResolver } from '@hookform/resolvers/yup'

const initialValues = {
	id: null,
	navn: '',
	beskrivelse: '',
	// opprettetAv: {},
	brukere: [],
}

const validation = Yup.object().shape({
	navn: Yup.string().trim().required('Feltet er påkrevd').max(30, 'Maksimalt 30 bokstaver'),
	beskrivelse: Yup.string()
		.trim()
		.required('Feltet er påkrevd')
		.max(200, 'Maksimalt 200 bokstaver'),
	brukere: Yup.array(),
})

export const OpprettRedigerTeam = ({ team = null, closeModal }) => {
	const erOppretting = !team

	const [isLoading, setIsLoading] = useState(false)
	const [error, setError] = useState('')

	// TODO: Trenger ikke opprettet av, utledes i BE
	// const { currentBruker } = useCurrentBruker()
	// console.log('currentBruker: ', currentBruker) //TODO - SLETT MEG

	const { brukere, loading: loadingBrukere } = useAlleBrukere()
	const brukerOptions = brukere?.map((bruker) => {
		return {
			value: bruker?.brukerId,
			label: bruker?.brukernavn,
		}
	})

	const mutate = useMatchMutate()
	const formMethods = useForm({
		mode: 'all',
		defaultValues: team ?? initialValues,
		resolver: yupResolver(validation),
	})

	console.log('formMethods.watch(): ', formMethods.watch()) //TODO - SLETT MEG

	const handleOpprettTeam = () => {
		setIsLoading(true)
		setError('')
		DollyApi.opprettTeam(formMethods.watch())
			.then((response) => {
				if (response.data?.error) {
					setError('Noe gikk galt under oppretting av team: ' + response.data?.error)
					setIsLoading(false)
				} else {
					closeModal()
					setIsLoading(false)
					return mutate(REGEX_BACKEND_TEAM)
				}
			})
			.catch((error) => {
				setError('Noe gikk galt under oppretting av team: ' + error.message)
				setIsLoading(false)
			})
	}

	const handleRedigerTeam = () => {
		setIsLoading(true)
		setError('')
		DollyApi.redigerTeam(formMethods.watch('id'), formMethods.watch())
			.then((response) => {
				if (response.data?.error) {
					setError('Noe gikk galt under redigering av team: ' + response.data?.error)
					setIsLoading(false)
				} else {
					closeModal()
					setIsLoading(false)
					return mutate(REGEX_BACKEND_TEAM)
				}
			})
			.catch((error) => {
				setError('Noe gikk galt under redigering av team: ' + error.message)
				setIsLoading(false)
			})
	}

	return (
		<FormProvider {...formMethods}>
			<Form
				onSubmit={formMethods.handleSubmit(erOppretting ? handleOpprettTeam : handleRedigerTeam)}
			>
				<h1>{team ? `Rediger team ${team.navn}` : 'Opprett team'}</h1>
				<div className="flexbox--flex-wrap" style={{ marginTop: '15px' }}>
					<FormTextInput name="navn" label="Navn på teamet" size="large" />
					<FormTextInput name="beskrivelse" label="Beskrivelse av teamet" size="xlarge" />
				</div>
				<div className="flexbox--full-width">
					<FormSelect
						name="brukere"
						label="Medlemmer i teamet"
						placeholder={loadingBrukere ? 'Laster brukere ...' : 'Velg brukere ...'}
						options={brukerOptions}
						isMulti={true}
						size="grow"
					/>
				</div>
				{error && <div className="skjemaelement__feilmelding">{error}</div>}
				<div className="dollymodal_buttons">
					<NavButton variant={'danger'} onClick={closeModal}>
						Avbryt
					</NavButton>
					<NavButton
						variant={'primary'}
						onClick={() => {
							formMethods.trigger(['navn', 'beskrivelse'])
							formMethods.setValue('navn', formMethods.getValues('navn'), {
								shouldValidate: true,
								shouldTouch: true,
							})
							formMethods.setValue('beskrivelse', formMethods.getValues('beskrivelse'), {
								shouldValidate: true,
								shouldTouch: true,
							})
						}}
						loading={isLoading}
					>
						{team ? 'Lagre endringer' : 'Opprett team'}
					</NavButton>
				</div>
			</Form>
		</FormProvider>
	)
}
