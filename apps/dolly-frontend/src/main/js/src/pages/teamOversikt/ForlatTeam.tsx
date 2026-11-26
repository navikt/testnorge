import React, { useState } from 'react'
import { BodyLong, Button, Dialog, ErrorMessage } from '@navikt/ds-react'
import { DollyApi } from '@/service/Api'

type Props = {
	team: { id: number; navn: string }
	brukerId?: string
	closeDialog: () => void
	mutate: () => void
}

export const ForlatTeam = ({ team, brukerId, closeDialog, mutate }: Props) => {
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
			<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
				<Dialog.Header withClosebutton={false}>
					<Dialog.Title>Forlat team</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BodyLong>{`Er du sikker på at du vil forlate team ${team.navn}?`}</BodyLong>
					{error && <ErrorMessage showIcon>{error}</ErrorMessage>}
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button variant="secondary" data-color="neutral">
							Avbryt
						</Button>
					</Dialog.CloseTrigger>
					<Button variant="danger" onClick={handleForlatTeam} loading={isLoading}>
						Ja, forlat team
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
