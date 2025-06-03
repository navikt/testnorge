import NavButton from '@/components/ui/button/NavButton/NavButton'
import React from 'react'

export const OpprettRedigerTeam = ({ team = null, closeModal, formMethods }) => {
	return (
		<>
			<h1>{team ? `Rediger team ${team.navn}` : 'Opprett team'}</h1>
			<div className="dollymodal_buttons">
				<NavButton variant={'danger'} onClick={closeModal}>
					Avbryt
				</NavButton>
				<NavButton variant={'primary'} onClick={formMethods?.handleSubmit()}>
					{team ? 'Lagre endringer' : 'Opprett team'}
				</NavButton>
			</div>
		</>
	)
}
