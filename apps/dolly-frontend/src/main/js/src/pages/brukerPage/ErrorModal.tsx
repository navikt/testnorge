import Icon from '@/components/ui/icon/Icon'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import React from 'react'

export const ErrorModal = ({ errorModalIsOpen, closeErrorModal }) => {
	return (
		<DollyModal isOpen={errorModalIsOpen} closeModal={closeErrorModal} width="40%" overflow="auto">
			<div className="slettModal">
				<div className="slettModal slettModal-content">
					<Icon size={50} kind="report-problem-circle" />
					<h1>Ukjent feil</h1>
					<h4>
						Ukjent feil ved henting av organisasjoner for bankid-bruker. Ta kontakt med en ansvarlig
						voksen for å løse problemet.
					</h4>
				</div>
				<div className="slettModal-actions">
					<NavButton onClick={() => closeErrorModal()} variant="primary">
						Tilbake
					</NavButton>
				</div>
			</div>
		</DollyModal>
	)
}
