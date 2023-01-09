import Button from '@/components/ui/button/Button'
import React from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import { FlyttPersonModal } from '@/components/ui/button/FlyttPersonButton/FlyttPersonModal'

export const FlyttPersonButton = ({ gruppeId, disabled }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	return (
		<>
			<Button
				onClick={openModal}
				className="svg-icon-blue-line"
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
