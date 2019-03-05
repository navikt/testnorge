import React, { Component, Fragment } from 'react'
import Knapp from 'nav-frontend-knapper'
import DollyModal from '~/components/modal/DollyModal'
import './SendOpenAm.less'

export default class SendOpenAm extends Component {
	state = {
		modalOpen: false
	}
	openModal = () => {
		this.setState({ modalOpen: true })
	}
	closeModal = () => {
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
					<Knapp
						type="standard"
						onClick={this.openModal}
						spinner={openAmFetching}
						autoDisableVedSpinner
					>
						Oppdater OpenAM
					</Knapp>
					<DollyModal
						isOpen={modalOpen}
						onRequestClose={this.closeModal}
						closeModal={this.closeModal}
						content={this._renderOpenAmModalContent()}
					/>
				</Fragment>
			)
		}

		return (
			<Knapp type="standard" onClick={sendToOpenAm} spinner={openAmFetching} autoDisableVedSpinner>
				Send til OpenAM
			</Knapp>
		)
	}

	_renderOpenAmModalContent = () => (
		<Fragment>
			<h1>Oppdater OpenAM</h1>
			Gruppen er allerede sendt til OpenAM. Dolly vil forsøke å sende alle testidenter på nytt. Er
			du sikker på at du vil utføre dette?
			<div className="dollymodal_buttons">
				<Knapp autoFocus type="standard" onClick={this.closeModal}>
					Avbryt
				</Knapp>
				<Knapp type="hoved" onClick={this.closeOnSend}>
					Utfør
				</Knapp>
			</div>
		</Fragment>
	)
}
