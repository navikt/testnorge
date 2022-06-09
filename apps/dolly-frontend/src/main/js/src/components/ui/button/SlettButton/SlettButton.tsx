import React from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Button from '~/components/ui/button/Button'
import Icon from '~/components/ui/icon/Icon'
import Loading from '~/components/ui/loading/Loading'

import './SlettModal.less'
import { useNavigate } from 'react-router-dom'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '~/utils/hooks/useMutate'

type Props = {
	action: Function
	gruppeId?: string | number
	loading: boolean
	children: any
	disabled?: boolean
	title?: string
	navigateHome?: boolean
}

export const SlettButton = ({
	action,
	gruppeId,
	loading,
	children,
	disabled = false,
	title,
	navigateHome = false,
}: Props) => {
	if (loading) return <Loading label="sletter..." />
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const navigate = useNavigate()
	const mutate = useMatchMutate()

	const getTitle = () => {
		if (title) return title
		return disabled ? 'Sletting er midlertidig utilgjengelig' : ''
	}

	return (
		<React.Fragment>
			<Button onClick={openModal} disabled={disabled} title={getTitle()} kind="trashcan">
				SLETT
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="slettModal">
					<div className="slettModal slettModal-content">
						<Icon size={50} kind="report-problem-circle" />
						<h1>Slett</h1>
						<h4>{children}</h4>
					</div>
					<div className="slettModal-actions">
						<NavButton onClick={closeModal}>Nei</NavButton>
						<NavButton
							onClick={() => {
								closeModal()
								gruppeId ? action(gruppeId) : action()
								mutate(REGEX_BACKEND_GRUPPER)
								navigateHome && navigate('/')
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
