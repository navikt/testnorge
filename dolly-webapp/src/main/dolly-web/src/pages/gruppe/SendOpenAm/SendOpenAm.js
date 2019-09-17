import React, { Component, Fragment } from 'react'
import Button from '~/components/ui/button/Button'
import PropTypes from 'prop-types'
import Loading from '~/components/ui/loading/Loading'
import './SendOpenAm.less'

export default class SendOpenAm extends Component {
	constructor(props) {
		super(props)
		this._isMounted = false // For å unngå memory leaks
	}

	state = {
		requestSent: false,
		showButton: true
	}

	static propTypes = {
		kind: PropTypes.string
	}

	static defaultProps = {
		kind: null
	}

	componentDidMount() {
		this._isMounted = true
	}

	componentWillUnmount() {
		this._isMounted = false
	}

	_hideOnClick = async (sendToOpenAm, bestillingId) => {
		this.setState({ showButton: false })
		try {
			await sendToOpenAm(bestillingId)
			this._isMounted && this.setState({ requestSent: true })
		} catch (err) {
			console.error(err)
		}
	}

	render() {
		const { sendToOpenAm, openAmFetching, bestillingId } = this.props

		if (openAmFetching & !this.state.showButton) {
			if (this.state.requestSent) return null
			return <Loading label="sender" className="openam-loading-spinner" />
		}

		return (
			this.state.showButton && (
				<Button
					className="flexbox--align-center openam-button"
					onClick={() => {
						this._hideOnClick(sendToOpenAm, bestillingId)
					}}
					kind="chevron-right"
				>
					SEND TIL OPENAM
				</Button>
			)
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
