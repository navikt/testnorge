import './SlettModal.less'
import { useNavigate } from 'react-router'
import {
	REGEX_BACKEND_BESTILLINGER,
	REGEX_BACKEND_GRUPPER,
	useMatchMutate,
} from '@/utils/hooks/useMutate'
import React from 'react'
import { BodyLong, Button, Dialog } from '@navikt/ds-react'
import { TrashIcon } from '@navikt/aksel-icons'

type Props = {
	action: Function
	gruppeId?: string | number
	bestillingId?: string | number
	loading: boolean
	disabled?: boolean
	navigateHome?: boolean
	autoMutate?: boolean
	slettType: string
}

// TODO: Test at alle typer sletting funker
export const SlettButton = ({
	action,
	gruppeId,
	bestillingId,
	loading,
	navigateHome = false,
	autoMutate = true,
	slettType,
}: Props) => {
	const navigate = useNavigate()
	const mutate = useMatchMutate()
	const slettMedId = bestillingId || gruppeId

	return (
		<Dialog>
			<Dialog.Trigger>
				<Button size="xsmall" variant="tertiary" icon={<TrashIcon aria-hidden />} loading={loading}>
					Slett
				</Button>
			</Dialog.Trigger>
			<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
				<Dialog.Header withClosebutton={false}>
					<Dialog.Title>{`Er du sikker på at du vil slette ${slettType}?`}</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BodyLong>{`Du er i ferd med å slette en ${slettType}. Denne handlingen kan ikke angres.`}</BodyLong>
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
							onClick={() => {
								const run = slettMedId ? action(slettMedId) : action()
								run?.then(() => {
									if (autoMutate) {
										if (slettMedId) {
											mutate(REGEX_BACKEND_BESTILLINGER)
										}
										return mutate(REGEX_BACKEND_GRUPPER)
									}
								})
								navigateHome && navigate('/')
							}}
						>
							{`Ja, slett ${slettType}`}
						</Button>
					</Dialog.CloseTrigger>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
