import DollyModal from '~/components/ui/modal/DollyModal'
import React, { useState } from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { v4 as _uuid } from 'uuid'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'
import Logger from '~/logger'
import { Textarea } from 'nav-frontend-skjema'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { useToggle } from 'react-use'

export const ForbedringModal = ({ closeModal }) => {
	const MAX_LENGTH = 2000
	const [uuid] = useState(_uuid())
	const [forbedring, setForbedring] = useState('')
	const [isAnonym, toggleAnonym] = useToggle(false)

	const sendForbedring = () => {
		Logger.log({
			event: `Ønsket forbedring fra Dollybruker`,
			message: forbedring,
			uuid: uuid,
			isAnonym: isAnonym
		})
		closeModal()
	}

	return (
		<ErrorBoundary>
			<DollyModal isOpen closeModal={closeModal} width="70%">
				<div className="modal">
					<h1>Ønske om forbedring eller ny funksjonalitet</h1>
					<br />
					<div className="skjemaelement textarea__container">
						<label className="skjemaelement__label">Jeg ønsker å være anonym</label>
						<DollyCheckbox onChange={toggleAnonym} isSwitch />
					</div>

					<br />
					<Textarea
						value={forbedring}
						label={'Forbedring/funksjonalitet'}
						placeholder={'Forsøk å være så spesifikk som mulig'}
						maxLength={MAX_LENGTH}
						onChange={event => setForbedring(event.target.value)}
						feil={
							forbedring.length > MAX_LENGTH
								? { feilmelding: 'Tilbakemelding inneholder for mange tegn' }
								: null
						}
					/>
					<ModalActionKnapper
						submitknapp="Send ønske"
						disabled={forbedring === '' || forbedring.length > MAX_LENGTH}
						onSubmit={sendForbedring}
						onAvbryt={closeModal}
						center
					/>
				</div>
			</DollyModal>
		</ErrorBoundary>
	)
}
