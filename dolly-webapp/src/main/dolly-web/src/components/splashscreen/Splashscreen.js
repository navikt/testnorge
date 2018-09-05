import React, { Component } from 'react'
import Modal from 'react-modal'
import DollyHjelpetekst from '~/utils/DollyHjelpetekst'
import Knapp from 'nav-frontend-knapper'

import './Splashscreen.less'

const customStyles = {
	content: {
		top: '50%',
		left: '50%',
		right: 'auto',
		bottom: 'auto',
		marginRight: '-50%',
		transform: 'translate(-50%, -50%)',
		width: '50vw'
	}
}

Modal.setAppElement('#root')

export default class Splashscreen extends Component {
	state = {
		isOpen: true
	}

	closeModal = () => {
		this.setState({ isOpen: false })
		this.props.setSplashscreenAccepted()
	}

	render() {
		return (
			<Modal style={customStyles} isOpen={this.state.isOpen}>
				<div className="splashscreen-container">
					<DollyHjelpetekst />
					<Knapp type="hoved" onClick={this.closeModal}>
						JEG FORSTÅR
					</Knapp>
				</div>
			</Modal>
		)
	}
}
