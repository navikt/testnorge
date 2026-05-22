import React from 'react'
import useBoolean from '@/utils/hooks/useBoolean'
import { FlyttPersonModal } from '@/components/ui/button/FlyttPersonButton/FlyttPersonModal'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Button } from '@navikt/ds-react'
import { LeaveIcon } from '@navikt/aksel-icons'

export const FlyttPersonButton = ({ gruppeId, disabled }) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	return (
		<>
			<Button
				data-testid={TestComponentSelectors.BUTTON_FLYTT_PERSONER}
				size="xsmall"
				variant="tertiary"
				icon={<LeaveIcon aria-hidden />}
				onClick={openModal}
				disabled={disabled}
				title={disabled ? 'Kan ikke flytte personer fra en tom gruppe' : undefined}
			>
				Flytt personer
			</Button>
			{modalIsOpen && (
				<FlyttPersonModal gruppeId={gruppeId} modalIsOpen={modalIsOpen} closeModal={closeModal} />
			)}
		</>
	)
}
