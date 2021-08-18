import React from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Button from '~/components/ui/button/Button'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'
import './LaasModal.less'

type LaasButton = {
	action: () => void
	loading: boolean
	children: string
}

export const LaasButton = ({ action, loading, children }: LaasButton) => {
	if (loading) return <Loading label="låser..." />
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)

	return (
		<React.Fragment>
			<Button onClick={openModal} kind="lock">
				LÅS
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="laasModal">
					<div className="laasModal laasModal-content">
						{
							// @ts-ignore
							<Icon size={50} kind="report-problem-circle" />
						}
						<h1>Lås gruppen</h1>
						<h4>{children}</h4>
					</div>
					<div className="laasModal-actions">
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
