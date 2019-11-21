import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import { DollyApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import { Line } from 'rc-progress'
import Knapp from 'nav-frontend-knapper'
import Icon from '~/components/ui/icon/Icon'

import './BestillingProgresjon.less'

export default class BestillingProgresjon extends PureComponent {
	static propTypes = {
		bestilling: PropTypes.object.isRequired
	}

	constructor(props) {
		super(props)

		this.PULL_INTERVAL = 1000
		this.TIMEOUT_BEFORE_HIDE = 2000
		this.TIME_BEFORE_WARNING_MESSAGE = 120

		this.state = {
			ferdig: props.bestilling.ferdig,
			antallLevert: props.bestilling.antallLevert,
			failureIntervalCounter: 0,
			failed: false,
			sistOppdatert: props.bestilling.sistOppdatert
		}
	}

	componentDidMount() {
		if (!this.state.ferdig) {
			this.interval = setInterval(() => this.getBestillingStatus(), this.PULL_INTERVAL)
		}
	}

	componentWillUnmount() {
		this.stopPolling()
	}

	stopPolling = () => clearInterval(this.interval)

	getBestillingStatus = async () => {
		const bestillingId = this.props.bestilling.id

		try {
			const { data } = await DollyApi.getBestillingStatus(bestillingId)

			if (data.ferdig) {
				this.stopPolling()
			}
			this.updateStatus(data)
		} catch (error) {
			console.error(error)
		}
	}

	updateStatus = data => {
		// Setter alltid status til IKKE FERDIG, sånn at vi kan vise
		// en kort melding som sier at prosessen er ferdig
		let newState = {
			ferdig: false,
			antallLevert: data.antallLevert,
			sistOppdatert: data.sistOppdatert
		}
		this.setState(newState)

		if (data.ferdig) {
			setTimeout(async () => {
				await this.props.getBestillinger() // state.ferdig = true
				await this.props.getGruppe()
			}, this.TIMEOUT_BEFORE_HIDE)
		} else {
			this.harBestillingFeilet(data.sistOppdatert)
		}
	}

	harBestillingFeilet = sistOppdatert => {
		const liveTimeStamp = new Date(sistOppdatert).getTime()
		const oldTimeStamp = new Date(this.state.sistOppdatert).getTime()

		if (liveTimeStamp == oldTimeStamp) {
			this.setState({ failureIntervalCounter: this.state.failureIntervalCounter + 1 })
			// Etter et bestemt intervall uten update av timestamp, setter bestilling til failed
			if (this.state.failureIntervalCounter == this.TIME_BEFORE_WARNING_MESSAGE) {
				this.setState({ failed: true })
			}
		} else {
			this.setState({ sistOppdatert: sistOppdatert, failureIntervalCounter: 0, failed: false })
		}
	}

	calculateStatus = () => {
		const total = this.props.bestilling.antallIdenter
		const { antallLevert } = this.state

		// Percent
		let percent = (100 / total) * antallLevert
		let text = `Opprettet ${antallLevert} av ${total}`

		// To indicate progress hvis ingenting har skjedd enda
		if (percent === 0) percent += 10

		if (antallLevert === total) text = `Ferdigstiller bestilling`

		const title = percent === 100 ? 'FERDIG' : 'AKTIV BESTILLING'

		return {
			percent,
			title,
			text
		}
	}

	_onCancelBtn = () => {
		this.setState({ ferdig: true }, () => {
			this.props.cancelBestilling(this.props.bestilling.id)
			this.stopPolling()
		})
	}

	render() {
		const status = this.calculateStatus()

		return (
			<Fragment>
				<div className="flexbox--space">
					<h5>
						<Loading onlySpinner /> {status.title}
					</h5>
					<span>{status.text}</span>
				</div>
				<div>
					<Line percent={status.percent} strokeWidth={0.5} trailWidth={0.5} strokeColor="#254b6d" />
				</div>
				{this.state.failed && (
					<div className="cancel-container">
						<div>
							<Icon kind={'report-problem-circle'} />
							<h5 className="feil-status-text">
								Dette tar lengre tid enn forventet. Noe kan ha gått galt med bestillingen din.
							</h5>
						</div>
						<Knapp type="fare" onClick={this._onCancelBtn}>
							AVBRYT BESTILLING
						</Knapp>
					</div>
				)}
			</Fragment>
		)
	}
}
