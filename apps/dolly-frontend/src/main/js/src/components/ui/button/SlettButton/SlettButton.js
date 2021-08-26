import React from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Button from '~/components/ui/button/Button'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'

import './SlettModal.less'

export const SlettButton = ({ action, loading, children }) => {
	if (loading) return <Loading label="sletter..." />
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	return (
		<React.Fragment>
			<Button onClick={openModal} kind="trashcan">
				SLETT
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="slettModal">
					<div className="slettModal slettModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>SLETT</h1>
						<h4>{children}</h4>
					</div>
					<div className="slettModal-actions">
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
