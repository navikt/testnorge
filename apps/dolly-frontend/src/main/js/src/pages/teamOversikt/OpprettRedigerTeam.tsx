import React, { useState } from 'react'
import { Controller, useForm } from 'react-hook-form'
import { DollyApi } from '@/service/Api'
import { useAlleBrukere, useCurrentBruker } from '@/utils/hooks/useBruker'
import * as Yup from 'yup'
import { yupResolver } from '@hookform/resolvers/yup'
import { TestComponentSelectors } from '#/mocks/Selectors'
import {
	Button,
	Dialog,
	ErrorMessage,
	TextField,
	UNSAFE_Combobox as Combobox,
	VStack,
} from '@navikt/ds-react'

const initialValues = {
	id: null,
	navn: '',
	beskrivelse: '',
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

const FORM_ID = 'opprett-rediger-team'

export const OpprettRedigerTeam = ({ team = null, closeDialog, mutate }) => {
	const erOppretting = !team

	const [isLoading, setIsLoading] = useState(false)
	const [error, setError] = useState('')

	const { currentBruker } = useCurrentBruker()
	const { brukere, loading: loadingBrukere } = useAlleBrukere()
	const brukereFiltrert = brukere?.filter(
		(bruker) => bruker?.brukertype !== 'TEAM' && bruker?.brukerId !== currentBruker?.brukerId,
	)
	const brukerOptions =
		brukereFiltrert?.map((bruker) => ({
			value: String(bruker?.brukerId),
			label: bruker?.brukernavn,
		})) || []

	const getDefaultValues = team
		? {
				id: team.id,
				navn: team.navn,
				beskrivelse: team.beskrivelse,
				brukere: team.brukere?.map((bruker) => bruker.brukerId),
			}
		: initialValues

	const formMethods = useForm({
		mode: 'all',
		defaultValues: getDefaultValues,
		resolver: yupResolver(validation),
	})
	const {
		register,
		control,
		formState: { errors },
	} = formMethods

	const handleOpprettTeam = () => {
		setIsLoading(true)
		setError('')
		DollyApi.opprettTeam(formMethods.watch())
			.then((response) => {
				if (response.data?.error) {
					setError('Noe gikk galt under oppretting av team: ' + response.data?.error)
					setIsLoading(false)
				} else {
					closeDialog()
					setIsLoading(false)
					return mutate()
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
					closeDialog()
					setIsLoading(false)
					return mutate()
				}
			})
			.catch((error) => {
				setError('Noe gikk galt under redigering av team: ' + error.message)
				setIsLoading(false)
			})
	}

	return (
		<Dialog open onOpenChange={(open) => !open && closeDialog()}>
			<Dialog.Popup closeOnOutsideClick={false}>
				<Dialog.Header>
					<Dialog.Title>{team ? `Rediger team ${team.navn}` : 'Opprett team'}</Dialog.Title>
					{!team && (
						<Dialog.Description>
							Fyll ut skjemaet for å opprette et nytt team. Du blir selv automatisk medlem av
							teamet.
						</Dialog.Description>
					)}
				</Dialog.Header>
				<Dialog.Body>
					<form
						id={FORM_ID}
						onSubmit={formMethods.handleSubmit(
							erOppretting ? handleOpprettTeam : handleRedigerTeam,
						)}
					>
						<VStack gap="space-16">
							<TextField
								{...register('navn')}
								label="Navn på teamet"
								error={errors.navn?.message}
								data-testid={TestComponentSelectors.INPUT_TEAM_NAVN}
							/>
							<TextField
								{...register('beskrivelse')}
								label="Beskrivelse av teamet"
								error={errors.beskrivelse?.message}
								data-testid={TestComponentSelectors.INPUT_TEAM_BESKRIVELSE}
							/>
							<Controller
								name="brukere"
								control={control}
								render={({ field }) => {
									const valgteBrukere = (field.value || []).map(String)
									return (
										<Combobox
											label="Medlemmer i teamet"
											options={brukerOptions}
											selectedOptions={brukerOptions.filter((option) =>
												valgteBrukere.includes(option.value),
											)}
											isLoading={loadingBrukere}
											isMultiSelect
											onToggleSelected={(value, isSelected) => {
												const bruker = brukereFiltrert?.find((b) => String(b?.brukerId) === value)
												const nyeBrukere = isSelected
													? [...(field.value || []), bruker?.brukerId ?? value]
													: (field.value || []).filter((id) => String(id) !== value)
												field.onChange(nyeBrukere)
											}}
										/>
									)
								}}
							/>
							{error && <ErrorMessage showIcon>{error}</ErrorMessage>}
						</VStack>
					</form>
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button variant="secondary">Avbryt</Button>
					</Dialog.CloseTrigger>
					<Button
						type="submit"
						form={FORM_ID}
						variant="primary"
						loading={isLoading}
						data-testid={TestComponentSelectors.BUTTON_TEAM_SUBMIT}
					>
						{team ? 'Lagre endringer' : 'Opprett team'}
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
