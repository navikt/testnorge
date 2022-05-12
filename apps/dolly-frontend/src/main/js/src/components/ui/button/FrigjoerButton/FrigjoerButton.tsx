import React from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Button from '~/components/ui/button/Button'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'

import './FrigjoerModal.less'

type Props = {
	action: Function
	loading: boolean
	disabled?: boolean
}

export const FrigjoerButton = ({ action, loading, disabled = false }: Props) => {
	if (loading) return <Loading label="frigjører..." />
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	return (
		<React.Fragment>
			<Button
				onClick={openModal}
				disabled={disabled}
				title={disabled ? 'Frigjøring/sletting er midlertidig utilgjengelig' : ''}
				kind="trashcan"
			>
				FRIGJØR/SLETT
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="frigjoerModal">
					<div className="frigjoerModal frigjoerModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Frigjør/slett</h1>
						<h4>
							Er du sikker på at du vil frigjøre denne personen? All ekstra informasjon lagt til på
							personen og via Dolly vil bli slettet og personen vil bli frigjort fra gruppen.
						</h4>
					</div>
					<div className="frigjoerModal-actions">
						<NavButton onClick={closeModal}>Nei</NavButton>
						<NavButton
							onClick={() => {
								closeModal()
								return action()
							}}
							type="hoved"
						>
							Ja, jeg er sikker
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
