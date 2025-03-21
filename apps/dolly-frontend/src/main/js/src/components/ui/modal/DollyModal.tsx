import Modal from 'react-modal'
import React, { ReactNode, useEffect } from 'react'
import './DollyModal.less'
import CloseButton from '@/components/ui/button/CloseButton/CloseButton'

const customStyles = {
	content: {
		top: '50%',
		left: '50%',
		right: 'auto',
		bottom: 'auto',
		marginRight: '-50%',
		transform: 'translate(-50%, -50%)',
		width: '25%',
		minWidth: '500px',
		overflow: 'auto',
		maxHeight: '80%',
	},
	overlay: {
		background: 'rgba(0,0,0,0.75)',
	},
}

type DollyModalProps = {
	children: ReactNode
	isOpen?: boolean
	closeModal: () => void
	noCloseButton?: boolean
	width?: string
	overflow?: string
	minWidth?: string
}

// Set the app element for accessibility
if (process.env.NODE_ENV !== 'test') {
	Modal.setAppElement('#root')
}

export const DollyModal: React.FC<DollyModalProps> = ({
	children,
	isOpen,
	closeModal,
	noCloseButton,
	width,
	overflow,
	minWidth,
}) => {
	useEffect(() => {
		if (width && isOpen) customStyles.content.width = width
		if (minWidth && isOpen) customStyles.content.minWidth = minWidth
		if (overflow && isOpen) customStyles.content.overflow = overflow
	}, [isOpen, width, minWidth, overflow])

	return (
		<Modal isOpen={isOpen} shouldCloseOnEsc onRequestClose={closeModal} style={customStyles}>
			<div className="dollymodal">
				{children}
				{!noCloseButton && <CloseButton onClick={closeModal} />}
			</div>
		</Modal>
	)
}
