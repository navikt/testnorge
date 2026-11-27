import OrganisasjonTilgangService from '@/service/services/organisasjonTilgang/OrganisasjonTilgangService'
import { TrashIcon, XMarkOctagonFillIcon } from '@navikt/aksel-icons'
import useBoolean from '@/utils/hooks/useBoolean'
import React, { useState } from 'react'
import { BodyLong, Button, Dialog, InfoCard } from '@navikt/ds-react'

type DeleteTypes = {
	orgNr: string
	navn: string
	mutate: Function
}

export const DeleteOrganisasjon = ({ orgNr, navn, mutate }: DeleteTypes) => {
	const [isLoading, setIsLoading, resetLoading] = useBoolean(false)
	const [dialogIsOpen, setDialogIsOpen] = useState(false)
	const [error, setError] = useState<Error | null>(null)

	const handleOpenChange = (open: boolean) => {
		if (open) {
			setError(null)
		}
		setDialogIsOpen(open)
	}

	const slettOrg = async () => {
		setIsLoading(true)
		await OrganisasjonTilgangService.deleteOrganisasjoner(orgNr)
			.then(() => {
				mutate().then(() => {
					resetLoading()
					setDialogIsOpen(false)
				})
			})
			.catch((error) => {
				setError(error)
				resetLoading()
			})
	}

	return (
		<Dialog open={dialogIsOpen} onOpenChange={handleOpenChange}>
			<Dialog.Trigger>
				<Button variant="tertiary" icon={<TrashIcon title="Slett tilgang" />} size="small" />
			</Dialog.Trigger>
			<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
				<Dialog.Header withClosebutton={false}>
					<Dialog.Title>Fjern tilgang</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BodyLong>{`Er du sikker på at du vil fjerne tilgang til Dolly for ${navn}?`}</BodyLong>
					{error && (
						<InfoCard data-color="danger" size="small" style={{ marginTop: '1rem' }}>
							<InfoCard.Message icon={<XMarkOctagonFillIcon aria-hidden />}>
								Feil ved sletting: {error.message}
							</InfoCard.Message>
						</InfoCard>
					)}
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button variant="secondary" data-color="neutral">
							Avbryt
						</Button>
					</Dialog.CloseTrigger>
					<Button variant="danger" onClick={slettOrg} loading={isLoading}>
						Ja, fjern tilgang
					</Button>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
