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
		const { content, isOpen, onRequestClose, closeModal, width } = this.props
		if (width) customStyles.content.width = width
		else customStyles.content.width = '25%'

		return (
			<Modal isOpen={isOpen} shouldCloseOnEsc onRequestClose={onRequestClose} style={customStyles}>
				<div className="dollymodal">
					{content ? content : <p>Du må sende inn content som props</p>}
					<Lukknapp onClick={closeModal} />
				</div>
			</Modal>
		)
	}
}
