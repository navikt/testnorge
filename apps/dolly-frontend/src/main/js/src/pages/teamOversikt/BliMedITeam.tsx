import React, { useState } from 'react'
import {
	BodyLong,
	Button,
	Dialog,
	ErrorMessage,
	UNSAFE_Combobox as Combobox,
} from '@navikt/ds-react'
import { DollyApi } from '@/service/Api'
import { useHentAlleTeam } from '@/utils/hooks/useTeam'

type Props = {
	brukerId?: string
	brukerTeams: Array<{ id: number }>
	closeDialog: () => void
	mutate: () => void
}

export const BliMedITeam = ({ brukerId, brukerTeams, closeDialog, mutate }: Props) => {
	const [isLoading, setIsLoading] = useState(false)
	const [error, setError] = useState('')
	const [valgtTeam, setValgtTeam] = useState<string | null>(null)
	const [valgtTeamError, setValgtTeamError] = useState('')

	const { alleTeam, loading: loadingTeams } = useHentAlleTeam()
	const filtrerteTeam = alleTeam?.filter((team) => !brukerTeams?.some((t) => t.id === team.id))
	const teamOptions =
		filtrerteTeam?.map((team) => ({
			value: String(team?.id),
			label: team?.navn,
		})) || []
	const valgtTeamOption = teamOptions.filter((option) => option.value === valgtTeam)

	const handleBliMedITeam = () => {
		if (!valgtTeam) {
			setValgtTeamError('Du må velge et team å bli med i')
			return
		}
		setIsLoading(true)
		setError('')
		DollyApi.leggTilBrukerITeam(valgtTeam, brukerId)
			.then((response) => {
				if (response.data?.error) {
					setError('Noe gikk galt: ' + response.data?.error)
					setIsLoading(false)
				} else {
					closeDialog()
					setIsLoading(false)
					return mutate()
				}
			})
			.catch((error) => {
				setError('Noe gikk galt: ' + error.message)
				setIsLoading(false)
			})
	}

	return (
		<Dialog open onOpenChange={(open) => !open && closeDialog()}>
			<Dialog.Popup width="medium" closeOnOutsideClick={false}>
				<Dialog.Header>
					<Dialog.Title>Bli med i team</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<Combobox
						label="Velg et team å bli med i"
						options={teamOptions}
						selectedOptions={valgtTeamOption}
						isLoading={loadingTeams}
						onToggleSelected={(value, isSelected) => {
							setValgtTeam(isSelected ? value : null)
							setValgtTeamError('')
						}}
						error={valgtTeamError}
						shouldAutocomplete
					/>
					{error && (
						<BodyLong as="div" style={{ marginTop: '1rem' }}>
							<ErrorMessage showIcon>{error}</ErrorMessage>
						</BodyLong>
					)}
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button variant="secondary">Avbryt</Button>
					</Dialog.CloseTrigger>
					<Button variant="primary" onClick={handleBliMedITeam} loading={isLoading}>
						Bli med i team
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
