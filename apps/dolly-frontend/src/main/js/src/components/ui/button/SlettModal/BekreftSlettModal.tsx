import React from 'react'
import { BodyLong, Button, Dialog } from '@navikt/ds-react'
import { TrashIcon } from '@navikt/aksel-icons'

type BekreftSlettModalProps = {
	tittel: string
	beskrivelse: string
	onBekreft: () => unknown
	disabled?: boolean
}

export const BekreftSlettModal = ({
	tittel,
	beskrivelse,
	onBekreft,
	disabled = false,
}: BekreftSlettModalProps) => (
	<Dialog>
		<Dialog.Trigger>
			<Button
				size="xsmall"
				variant="tertiary"
				icon={<TrashIcon title="Slett" />}
				disabled={disabled}
			/>
		</Dialog.Trigger>
		<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
			<Dialog.Header withClosebutton={false}>
				<Dialog.Title>{tittel}</Dialog.Title>
			</Dialog.Header>
			<Dialog.Body>
				<BodyLong>{beskrivelse}</BodyLong>
			</Dialog.Body>
			<Dialog.Footer>
				<Dialog.CloseTrigger>
					<Button variant="secondary" data-color="neutral">
						Avbryt
					</Button>
				</Dialog.CloseTrigger>
				<Dialog.CloseTrigger>
					<Button variant="danger" onClick={() => onBekreft()}>
						Ja, slett
					</Button>
				</Dialog.CloseTrigger>
			</Dialog.Footer>
		</Dialog.Popup>
	</Dialog>
)
