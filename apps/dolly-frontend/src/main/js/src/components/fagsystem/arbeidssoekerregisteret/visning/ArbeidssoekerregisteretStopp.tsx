import { useState } from 'react'
import { ArbeidssoekerregisteretApi } from '@/service/Api'
import { Alert, Button, Dialog } from '@navikt/ds-react'
import { CircleSlashIcon } from '@navikt/aksel-icons'

type Props = {
	ident: string
	onStopped: () => void
}

export const ArbeidssoekerregisteretStopp = ({ ident, onStopped }: Props) => {
	const [dialogOpen, setDialogOpen] = useState(false)
	const [loading, setLoading] = useState(false)
	const [errorMessage, setErrorMessage] = useState<string | null>(null)

	const handleOpenChange = (open: boolean) => {
		if (!open && loading) {
			return
		}
		if (open) {
			setErrorMessage(null)
		}
		setDialogOpen(open)
	}

	const handleStop = async () => {
		setLoading(true)
		setErrorMessage(null)

		try {
			await ArbeidssoekerregisteretApi.stoppRegistrering(ident)
		} catch (error) {
			setErrorMessage(error instanceof Error ? error.message : 'Kunne ikke stoppe registreringen.')
			setLoading(false)
			return
		}

		setLoading(false)
		setDialogOpen(false)
		onStopped()
	}

	return (
		<Dialog open={dialogOpen} onOpenChange={handleOpenChange}>
			<Dialog.Trigger>
				<Button variant="tertiary" size="xsmall" icon={<CircleSlashIcon aria-hidden />}>
					Stopp
				</Button>
			</Dialog.Trigger>
			<Dialog.Popup role="alertdialog">
				<Dialog.Header>
					<Dialog.Title>Stopp arbeidssøkerregistrering</Dialog.Title>
					<Dialog.Description>
						Er du sikker på at du vil stoppe arbeidssøkerregistreringen?
					</Dialog.Description>
				</Dialog.Header>
				{errorMessage && (
					<Dialog.Body>
						<Alert variant="error">{errorMessage}</Alert>
					</Dialog.Body>
				)}
				<Dialog.Footer>
					<Dialog.CloseTrigger disabled={loading}>
						<Button variant="secondary" disabled={loading}>
							Avbryt
						</Button>
					</Dialog.CloseTrigger>
					<Button variant="danger" onClick={handleStop} loading={loading}>
						Ja, stopp registreringen
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
