import React, { Component, Fragment } from 'react'
import Knapp from 'nav-frontend-knapper'
import Modal from 'react-modal'
import Lukknapp from 'nav-frontend-lukknapp'

import './SendOpenAm.less'

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
		overflow: 'inherit'
	}
}

Modal.setAppElement('#root')

export default class SendOpenAm extends Component {
	state = {
		modalOpen: false
	}

	open = () => {
		this.setState({ modalOpen: true })
	}
	close = () => {
		this.setState({ modalOpen: false })
	}

	closeOnSend = () => {
		this.setState({ modalOpen: false }, () => this.props.sendToOpenAm())
	}

	render() {
		const { sendToOpenAm, openAmFetching, openAmResponse, gruppe } = this.props
		const { modalOpen } = this.state

		if (gruppe.openAmSent || openAmResponse) {
			return (
				<Fragment>
					{openAmResponse && <span className="openam-status">Gruppen ble sendt!</span>}
					<Knapp type="standard" onClick={this.open} spinner={openAmFetching} autoDisableVedSpinner>
						Oppdater OpenAM
					</Knapp>
					<Modal
						isOpen={modalOpen}
						onRequestClose={this.close}
						shouldCloseOnEsc
						style={customStyles}
					>
						<div className="openam-modal">
							<h1>Oppdater OpenAM</h1>
							Gruppen er allerede sendt til OpenAM. Dolly vil forsøke å sende alle testidenter på
							nytt. Er du sikker på at du vil utføre dette?
							<div className="openam-modal_buttons">
								<Knapp autoFocus type="standard" onClick={this.close}>
									Avbryt
								</Knapp>
								<Knapp type="hoved" onClick={this.closeOnSend}>
									Utfør
								</Knapp>
							</div>
							<Lukknapp onClick={this.close} />
						</div>
					</Modal>
				</Fragment>
			)
		}

		return (
			<Knapp type="standard" onClick={sendToOpenAm} spinner={openAmFetching} autoDisableVedSpinner>
				Send til OpenAM
			</Knapp>
		)
	}
}
