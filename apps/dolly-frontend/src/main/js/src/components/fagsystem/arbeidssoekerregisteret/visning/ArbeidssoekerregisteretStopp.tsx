import { useState } from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyApi } from '@/service/Api'
import { Alert, BodyShort, Button, HStack, Modal, VStack } from '@navikt/ds-react'
import { CircleSlashIcon } from '@navikt/aksel-icons'

type Props = {
	ident: string
	onStopped: () => void
}

export const ArbeidssoekerregisteretStopp = ({ ident, onStopped }: Props) => {
	const [modalOpen, openModal, closeModal] = useBoolean(false)
	const [loading, setLoading] = useState(false)
	const [errorMessage, setErrorMessage] = useState<string | null>(null)

	const handleStop = async () => {
		setLoading(true)
		setErrorMessage(null)

		try {
			await DollyApi.stoppArbeidssoekerregisteret(ident)
		} catch (error) {
			setErrorMessage(error instanceof Error ? error.message : 'Kunne ikke stoppe registreringen.')
			setLoading(false)
			return
		}

		setLoading(false)
		closeModal()
		onStopped()
	}

	return (
		<>
			<Button
				variant="tertiary"
				size="xsmall"
				icon={<CircleSlashIcon aria-hidden />}
				onClick={() => {
					setErrorMessage(null)
					openModal()
				}}
			>
				Stopp
			</Button>
			<Modal
				open={modalOpen}
				onClose={closeModal}
				header={{ heading: 'Stopp arbeidssøkerregistrering', closeButton: true }}
				size="small"
				closeOnBackdropClick={false}
			>
				<Modal.Body>
					<VStack gap="space-16">
						<BodyShort>Er du sikker på at du vil stoppe arbeidssøkerregistreringen?</BodyShort>
						{errorMessage && (
							<Alert variant="error" size="small">
								{errorMessage}
							</Alert>
						)}
					</VStack>
				</Modal.Body>
				<Modal.Footer>
					<HStack gap="space-12" justify="end">
						<Button variant="secondary" onClick={closeModal} disabled={loading}>
							Avbryt
						</Button>
						<Button variant="danger" onClick={handleStop} loading={loading}>
							Ja, stopp registreringen
						</Button>
					</HStack>
				</Modal.Footer>
			</Modal>
		</>
	)
}
