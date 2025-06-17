import NavButton from '@/components/ui/button/NavButton/NavButton'
import React, { useState } from 'react'
import { DollyApi } from '@/service/Api'
import { DollySelect } from '@/components/ui/form/inputs/select/Select'

import { useHentAlleTeam } from '@/utils/hooks/useTeam'

export const BliMedITeam = ({ brukerId, brukerTeams, closeModal, mutate }) => {
	const [isLoading, setIsLoading] = useState(false)
	const [error, setError] = useState('')
	const [valgtTeam, setValgtTeam] = useState(null)
	const [valgtTeamError, setValgtTeamError] = useState('')

	const { alleTeam, loading: loadingTeams } = useHentAlleTeam()
	const filtrerteTeam = alleTeam?.filter((team) => !brukerTeams?.some((t) => t.id === team.id))
	const teamOptions = filtrerteTeam?.map((team) => {
		return {
			value: team?.id,
			label: team?.navn,
		}
	})

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
					closeModal()
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
		<>
			<h1>Bli med i team</h1>
			<div className="flexbox--full-width" style={{ marginTop: '15px' }}>
				<DollySelect
					name="team"
					value={valgtTeam}
					onChange={(e) => {
						setValgtTeam(e?.value)
						setValgtTeamError('')
					}}
					label="Velg et team å bli med i"
					placeholder={loadingTeams ? 'Laster team ...' : 'Velg team ...'}
					options={teamOptions}
					size="grow"
					isClearable={false}
					feil={valgtTeamError || null}
				/>
			</div>
			{error && <div className="skjemaelement__feilmelding">{error}</div>}
			<div className="dollymodal_buttons">
				<NavButton variant={'danger'} onClick={closeModal}>
					Avbryt
				</NavButton>
				<NavButton variant={'primary'} onClick={handleBliMedITeam} loading={isLoading}>
					Bli med i team
				</NavButton>
			</div>
		</>
	)
}
