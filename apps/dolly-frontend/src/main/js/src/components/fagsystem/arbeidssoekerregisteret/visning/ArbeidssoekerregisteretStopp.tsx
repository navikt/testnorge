import { useId, useState } from 'react'
import { ArbeidssoekerregisteretApi } from '@/service/Api'
import { Alert, BodyLong, Button, Dialog, Heading, VStack } from '@navikt/ds-react'
import { CircleSlashIcon } from '@navikt/aksel-icons'

type Props = {
	ident: string
	onStopped: () => void
}

export const ArbeidssoekerregisteretStopp = ({ ident, onStopped }: Props) => {
	const titleId = useId()
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
			<Dialog.Popup role="alertdialog" aria-labelledby={titleId}>
				<Dialog.Header>
					<Heading id={titleId} className="aksel-dialog__title" level="2" size="medium">
						Stopp arbeidssøkerregistrering
					</Heading>
				</Dialog.Header>
				<Dialog.Body>
					<VStack gap="space-16">
						<BodyLong>Er du sikker på at du vil stoppe arbeidssøkerregistreringen?</BodyLong>
						{errorMessage && <Alert variant="error">{errorMessage}</Alert>}
					</VStack>
				</Dialog.Body>
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
