import React from 'react'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import useBoolean from '@/utils/hooks/useBoolean'
import DollyModal from '@/components/ui/modal/DollyModal'
import Icon from '@/components/ui/icon/Icon'

import './AvbrytModal.less'
import { TestComponentSelectors } from '#/mocks/Selectors'

type Props = {
	action: Function
	children: string
}

export const AvbrytButton = ({ action, children }: Props) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	return (
		<React.Fragment>
			<NavButton
				data-testid={TestComponentSelectors.BUTTON_AVBRYT}
				variant={'danger'}
				onClick={openModal}
			>
				Avbryt
			</NavButton>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="fit-content" overflow="auto">
				<div className="avbrytModal">
					<div className="avbrytModal avbrytModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Avbryt bestilling</h1>
						<h4>{children}</h4>
					</div>
					<div className="avbrytModal-actions">
						<NavButton onClick={closeModal} variant={'secondary'}>
							Nei
						</NavButton>
						<NavButton
							data-testid={TestComponentSelectors.BUTTON_BEKREFT}
							onClick={() => {
								closeModal()
								return action()
							}}
							variant={'primary'}
						>
							Ja, jeg er sikker
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
