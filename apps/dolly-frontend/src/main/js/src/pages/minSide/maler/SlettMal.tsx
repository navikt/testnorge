import { BodyLong, Button, Dialog } from '@navikt/ds-react'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { TrashIcon } from '@navikt/aksel-icons'
import React from 'react'
import { DollyApi } from '@/service/Api'
import { MalType } from '@/pages/minSide/maler/Maloversikt'
import { tenorSlettPersonMal } from '@/service/services/templatesearch/TemplateSearch'

interface SlettMalProps {
	id: number
	type: string
	mutate: () => void
}

export const SlettMal = ({ id, type, mutate }: SlettMalProps) => {
	const slettMal = () => {
		switch (type) {
			case MalType.ORGANISASJON:
				return DollyApi.slettMalOrganisasjon(id).then(() => mutate())
			case MalType.PERSON:
				return DollyApi.slettMal(id).then(() => mutate())
			case MalType.TENORSOEK:
				return tenorSlettPersonMal(id).then(() => mutate())
			default:
				return null
		}
	}

	return (
		<Dialog>
			<Dialog.Trigger>
				<Button
					data-testid={TestComponentSelectors.BUTTON_MALER_SLETT}
					variant={'tertiary'}
					icon={<TrashIcon title="Slett" />}
					size={'small'}
				/>
			</Dialog.Trigger>
			<Dialog.Popup role="alertdialog" closeOnOutsideClick={false}>
				<Dialog.Header withClosebutton={false}>
					<Dialog.Title>Er du sikker på at du vil slette denne malen?</Dialog.Title>
				</Dialog.Header>
				<Dialog.Body>
					<BodyLong>Du er i ferd med å slette en mal. Denne handlingen kan ikke angres.</BodyLong>
				</Dialog.Body>
				<Dialog.Footer>
					<Dialog.CloseTrigger>
						<Button variant="secondary" data-color="neutral">
							Avbryt
						</Button>
					</Dialog.CloseTrigger>
					<Dialog.CloseTrigger>
						<Button
							data-testid={TestComponentSelectors.BUTTON_MALER_SLETT_BEKREFT}
							variant="danger"
							onClick={() => slettMal()}
						>
							Ja, slett mal
						</Button>
					</Dialog.CloseTrigger>
				</Dialog.Footer>
			</Dialog.Popup>
		</Dialog>
	)
}
