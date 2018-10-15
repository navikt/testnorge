import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { Line } from 'rc-progress'
import { DollyApi } from '~/service/Api'
import Loading from '~/components/loading/Loading'

import './BestillingStatus.less'

export default class BestillingStatus extends PureComponent {
	static propTypes = {
		bestilling: PropTypes.object.isRequired,
		onGroupUpdate: PropTypes.func.isRequired
	}

	constructor(props) {
		super(props)

		this.PULL_INTERVAL = 1000
		this.TIMEOUT_BEFORE_HIDE = 2000

		this.state = {
			ferdig: props.bestilling.ferdig,
			antallKlare: props.bestilling.personStatus.length,
			sistOppdatert: props.bestilling.sistOppdatert
		}
	}

	componentDidMount() {
		if (!this.state.ferdig)
			this.interval = setInterval(() => this.getBestillingStatus(), this.PULL_INTERVAL)
	}

	componentWillUnmount() {
		this.stopPolling()
	}

	stopPolling = () => clearInterval(this.interval)

	getBestillingStatus = async () => {
		// console.log('getBestillingStatus()')

		const bestillingId = this.props.bestilling.id

		try {
			const { data } = await DollyApi.getBestillingStatus(bestillingId)
			// console.log('res', data)

			// Should we stop polling?
			if (data.ferdig) this.stopPolling()

			// Update state
			this.updateStatus(data)
		} catch (error) {
			console.log('error', error)
		}
	}

	updateStatus = data => {
		// Setter alltid status til IKKE FERDIG, sånn at vi kan vise en kort melding som sier at prosessen er ferdig
		let newState = {
			ferdig: false,
			antallKlare: data.personStatus.length,
			sistOppdatert: data.sistOppdatert
		}

		this.setState(newState)

		if (data.ferdig) {
			setTimeout(() => {
				// Update groups
				this.props.onGroupUpdate()
			}, this.TIMEOUT_BEFORE_HIDE)
		}
	}

	calculateStatus = () => {
		const total = this.props.bestilling.antallIdenter
		const { antallKlare } = this.state

		// Percent
		let percent = (100 / total) * antallKlare
		let text = `Oppretter ${antallKlare + 1} av ${total}`

		// To indicate progress hvis ingenting har skjedd enda
		if (percent === 0) percent += 10

		if (antallKlare === total) text = `ferdigstiller bestilling`

		const title = percent === 100 ? 'FERDIG' : 'AKTIV BESTILLING'

		return {
			percent,
			title,
			text
		}
	}

	render() {
		if (this.state.ferdig) return false

		const status = this.calculateStatus()

		return (
			<div className="bestilling-status">
				<div className="flexbox--space">
					<h5>
						<Loading onlySpinner />
						{status.title}
					</h5>
					<span>{status.text}</span>
				</div>
				<div className="rc-progress-wrapper">
					<Line percent={status.percent} strokeWidth={0.5} trailWidth={0.5} strokeColor="#254b6d" />
				</div>
			</div>
		)
	}
}
