import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import React from 'react'
import { BodyLong, Button, Dialog } from '@navikt/ds-react'
import { TrashIcon } from '@navikt/aksel-icons'

type Props = {
	slettPerson: Function
	loading: boolean
	disabled?: boolean
}

export const FrigjoerButton = ({ slettPerson, loading, disabled = false }: Props) => {
	const mutate = useMatchMutate()

	return (
		<Dialog>
			<Dialog.Trigger>
				<Button
					size="xsmall"
					variant="tertiary"
					icon={<TrashIcon aria-hidden />}
					loading={loading}
					disabled={disabled}
					title={disabled ? 'Frigjøring/sletting er midlertidig utilgjengelig' : ''}
				>
					Frigjør/slett
				</Button>
			</Dialog.Trigger>
			<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
				<Dialog.Header withClosebutton={false}>
					<Dialog.Title>Er du sikker på at du vil frigjøre denne personen?</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BodyLong>
						Du er i ferd med å frigjøre en Testnorge-person. All ekstra informasjon lagt til på
						personen via Dolly vil bli slettet, og personen vil bli frigjort fra gruppen.
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
							onClick={() => {
								slettPerson().then(() => mutate(REGEX_BACKEND_GRUPPER))
							}}
						>
							{`Ja, frigjør person`}
						</Button>
					</Dialog.CloseTrigger>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
