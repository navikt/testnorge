import { DollyModal } from '@/components/ui/modal/DollyModal'
import React, { useState } from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { v4 as _uuid } from 'uuid'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { useToggle } from 'react-use'
import dolly from '@/favicon.ico'
import Icon from '@/components/ui/icon/Icon'
import { useBrukerProfilBilde, useCurrentBruker } from '@/utils/hooks/useBruker'
import { Textarea } from '@navikt/ds-react'
import { Logger } from '@/logger/Logger'
import { TestComponentSelectors } from '#/mocks/Selectors'

export const ForbedringModal = ({ closeModal }) => {
	const { brukerBilde } = useBrukerProfilBilde()
	const { currentBruker } = useCurrentBruker()

	const MAX_LENGTH = 2000
	const [uuid] = useState(_uuid())
	const [forbedring, setForbedring] = useState('')
	const [isAnonym, toggleAnonym] = useToggle(false)

	const sendForbedring = () => {
		Logger.log({
			event: `Ønsket forbedring fra Dollybruker`,
			message: forbedring,
			uuid: uuid,
			isAnonym: isAnonym,
			brukerType: currentBruker.brukertype,
		})
		closeModal()
	}

	return (
		<ErrorBoundary>
			<DollyModal isOpen closeModal={closeModal} width="70%">
				<h1>Ønske om forbedring eller ny funksjonalitet</h1>
				<br />
				<div className="modal-content">
					{isAnonym ? (
						<div>
							<Icon kind="user" fontSize={'2rem'} className="bruker-ikon" />
						</div>
					) : (
						<img alt="Profilbilde" src={brukerBilde || dolly} />
					)}

					<div className="modal-input">
						<Textarea
							data-testid={TestComponentSelectors.INPUT_FORBEDRING_MODAL}
							value={forbedring}
							label={'Forbedring/funksjonalitet'}
							placeholder={'Forsøk å være så spesifikk som mulig'}
							maxLength={MAX_LENGTH}
							onChange={(event) => setForbedring(event.target.value)}
							error={forbedring.length > MAX_LENGTH && 'Tilbakemelding inneholder for mange tegn'}
							autoFocus
						/>
						<div className="skjemaelement textarea__container">
							<DollyCheckbox
								data-testid={TestComponentSelectors.CHECKBOX_FORBEDRING_ANONYM}
								label="Jeg ønsker å være anonym"
								onChange={toggleAnonym}
							/>
						</div>
					</div>
				</div>
				<ModalActionKnapper
					data-testid={TestComponentSelectors.BUTTON_SEND_FORBEDRINGSOENSKE}
					submitknapp="Send ønske"
					disabled={forbedring === '' || forbedring.length > MAX_LENGTH}
					onSubmit={sendForbedring}
					onAvbryt={closeModal}
					center
				/>
			</DollyModal>
		</ErrorBoundary>
	)
}
