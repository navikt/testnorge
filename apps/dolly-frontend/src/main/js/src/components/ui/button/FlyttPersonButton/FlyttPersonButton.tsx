import Button from '@/components/ui/button/Button'
import React from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import { FlyttPersonModal } from '@/components/ui/button/FlyttPersonButton/FlyttPersonModal'
import { TestComponentSelectors } from '#/mocks/Selectors'

export const FlyttPersonButton = ({ gruppeId, disabled }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	return (
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_FLYTT_PERSONER}
				onClick={openModal}
				kind="flytt"
				disabled={disabled}
				title={disabled ? 'Kan ikke flytte personer fra en tom gruppe' : null}
			>
				FLYTT PERSONER
			</Button>
			{modalIsOpen && (
				<FlyttPersonModal gruppeId={gruppeId} modalIsOpen={modalIsOpen} closeModal={closeModal} />
			)}
		</>
	)
}
