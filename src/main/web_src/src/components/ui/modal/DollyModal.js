import Modal from 'react-modal'
import React, { PureComponent } from 'react'
import Lukknapp from 'nav-frontend-lukknapp'
import './DollyModal.less'

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
		maxHeight: '80%'
	},
	overlay: {
		background: 'rgba(0,0,0,0.75)'
	}
}

Modal.setAppElement('#root')

export default class DollyModal extends PureComponent {
	render() {
		const { children, isOpen, closeModal, noCloseButton, width, overflow, minWidth } = this.props

		if (width && isOpen) customStyles.content.width = width
		if (minWidth && isOpen) customStyles.content.width = minWidth
		if (overflow && isOpen) customStyles.content.overflow = overflow

		return (
			<Modal isOpen={isOpen} shouldCloseOnEsc onRequestClose={closeModal} style={customStyles}>
				<div className="dollymodal">
					{children}
					{!noCloseButton && <Lukknapp onClick={closeModal} />}
				</div>
			</Modal>
		)
	}
}
