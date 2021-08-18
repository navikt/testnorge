import React, { ReactChildren } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Icon from '~/components/ui/icon/Icon'

import './AvbrytModal.less'

type data = {
	action: Function
	children: ReactChildren
}

export const AvbrytButton = ({ action, children }: data) => {
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	return (
		<React.Fragment>
			<NavButton type={'fare'} onClick={openModal}>
				AVBRYT
			</NavButton>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="fit-content" overflow="auto">
				<div className="avbrytModal">
					<div className="avbrytModal avbrytModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Avbryt bestilling</h1>
						<h4>{children}</h4>
					</div>
					<div className="avbrytModal-actions">
						<NavButton onClick={closeModal}>NEI</NavButton>
						<NavButton
							onClick={() => {
								closeModal()
								return action()
							}}
							type="hoved"
						>
							JA, JEG ER SIKKER
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
