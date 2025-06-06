import Icon from '@/components/ui/icon/Icon'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import React, { useState } from 'react'
import { DollyApi } from '@/service/Api'

export const ForlatTeam = ({ team, brukerId, closeModal, mutate }) => {
	const [isLoading, setIsLoading] = useState(false)
	const [error, setError] = useState('')

	const handleForlatTeam = () => {
		setIsLoading(true)
		setError('')
		DollyApi.fjernBrukerFraTeam(team.id, brukerId)
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
		<div className="slettModal">
			<Icon size={50} kind="report-problem-circle" />
			<div className="slettModal slettModal-content">
				<h1>Forlat team</h1>
				<h4>{`Er du sikker på at du vil forlate team ${team.navn}?`}</h4>
				{error && <div className="skjemaelement__feilmelding">{error}</div>}
			</div>
			<div className="dollymodal_buttons">
				<NavButton variant={'secondary'} onClick={closeModal}>
					Nei
				</NavButton>
				<NavButton variant={'primary'} onClick={handleForlatTeam} loading={isLoading}>
					Ja, jeg er sikker
				</NavButton>
			</div>
		</div>
	)
}
