import React from 'react'
import Knapp from 'nav-frontend-knapper'
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
		<div className="flexbox--align-center--justify-end">
			<Button onClick={openModal} className="flexbox--align-center" kind="trashcan">
				SLETT
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%">
				<div className="slettModal">
					<div className="slettModal slettModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>SLETT</h1>
						<h4>{children}</h4>
					</div>
					<div className="slettModal-actions">
						<Knapp onClick={closeModal}>NEI</Knapp>
						<Knapp onClick={action} type="hoved" className="flexbox--align-center">
							JA, JEG ER SIKKER
						</Knapp>
					</div>
				</div>
			</DollyModal>
		</div>
	)
}
