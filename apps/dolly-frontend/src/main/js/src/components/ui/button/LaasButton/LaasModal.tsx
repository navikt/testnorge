import React from 'react'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import { BodyLong, Dialog, Button } from '@navikt/ds-react'
import { PadlockLockedIcon } from '@navikt/aksel-icons'

type LaasModalProps = {
	action: Function
	loading: boolean
	gruppeId: string | number
	autoMutate?: boolean
}

export const LaasModal = ({ action, gruppeId, loading, autoMutate = true }: LaasModalProps) => {
	const matchMutate = useMatchMutate()

	return (
		<Dialog>
			<Dialog.Trigger>
				<Button
					size="xsmall"
					variant="tertiary"
					icon={<PadlockLockedIcon aria-hidden />}
					loading={loading}
				>
					Lås
				</Button>
			</Dialog.Trigger>
			<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
				<Dialog.Header withClosebutton={false}>
					<Dialog.Title>Er du sikker på at du vil låse denne gruppen?</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BodyLong>
						En gruppe som er låst kan ikke endres, og blir heller ikke påvirket av prodlast i
						samhandlermiljøet (Q1). Når gruppen er låst må du kontakte Team Dolly dersom du ønsker å
						låse den opp igjen.
					</BodyLong>
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button variant="secondary" data-color="neutral">
							Avbryt
						</Button>
					</Dialog.CloseTrigger>
					<Dialog.CloseTrigger>
						<Button
							variant="danger"
							onClick={async () => {
								await action(gruppeId)
								if (autoMutate) {
									setTimeout(() => {
										matchMutate(REGEX_BACKEND_GRUPPER)
										matchMutate(REGEX_BACKEND_BESTILLINGER)
									}, 300)
								}
							}}
						>
							Ja, lås gruppe
						</Button>
					</Dialog.CloseTrigger>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
