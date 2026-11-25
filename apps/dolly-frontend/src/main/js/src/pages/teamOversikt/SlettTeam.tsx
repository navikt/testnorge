import React, { useState } from 'react'
import { BodyLong, Button, Dialog, ErrorMessage } from '@navikt/ds-react'
import { DollyApi } from '@/service/Api'

type Props = {
	team: { id: number; navn: string }
	closeModal: () => void
	mutate: () => void
}

export const SlettTeam = ({ team, closeModal, mutate }: Props) => {
	const [isLoading, setIsLoading] = useState(false)
	const [error, setError] = useState('')

	const handleSlettTeam = () => {
		setIsLoading(true)
		setError('')
		DollyApi.slettTeam(team.id)
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
		<Dialog open onOpenChange={(open) => !open && closeModal()}>
			<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
				<Dialog.Header withClosebutton={false}>
					<Dialog.Title>Slett team</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BodyLong>{`Er du sikker på at du vil slette team ${team.navn}? Denne handlingen kan ikke angres.`}</BodyLong>
					{error && <ErrorMessage showIcon>{error}</ErrorMessage>}
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button variant="secondary" data-color="neutral">
							Avbryt
						</Button>
					</Dialog.CloseTrigger>
					<Button variant="danger" onClick={handleSlettTeam} loading={isLoading}>
						Ja, slett team
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
