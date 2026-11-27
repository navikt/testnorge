import React from 'react'
import { BodyLong, Button, Dialog } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'

type Props = {
	action: () => unknown
	children: string
}

export const AvbrytButton = ({ action, children }: Props) => (
	<Dialog>
		<Dialog.Trigger>
			<Button data-testid={TestComponentSelectors.BUTTON_AVBRYT} variant="danger">
				Avbryt
			</Button>
		</Dialog.Trigger>
		<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
			<Dialog.Header withClosebutton={false}>
				<Dialog.Title>Avbryt bestilling</Dialog.Title>
			</Dialog.Header>
			<Dialog.Body>
				<BodyLong>{children}</BodyLong>
			</Dialog.Body>
			<Dialog.Footer>
				<Dialog.CloseTrigger>
					<Button variant="secondary" data-color="neutral">
						Nei
					</Button>
				</Dialog.CloseTrigger>
				<Dialog.CloseTrigger>
					<Button
						data-testid={TestComponentSelectors.BUTTON_BEKREFT}
						variant="danger"
						onClick={() => action()}
					>
						Ja, avbryt bestillingen
					</Button>
				</Dialog.CloseTrigger>
			</Dialog.Footer>
		</Dialog.Popup>
	</Dialog>
)
