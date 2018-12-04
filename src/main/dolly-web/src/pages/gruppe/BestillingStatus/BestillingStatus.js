import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { Line } from 'rc-progress'
import { DollyApi } from '~/service/Api'
import Loading from '~/components/loading/Loading'
import BestillingProgress from './BestillingProgress/BestillingProgress'
import MiljoeStatus from './MiljoeStatus/MiljoeStatus'
import './BestillingStatus.less'
import _find from 'lodash/find'

export default class BestillingStatus extends PureComponent {
	static propTypes = {
		bestilling: PropTypes.object.isRequired
	}

	constructor(props) {
		super(props)

		this.PULL_INTERVAL = 1000
		this.TIMEOUT_BEFORE_HIDE = 2000

		this.state = {
			ferdig: props.bestilling.ferdig,
			antallKlare: props.bestilling.personStatus ? props.bestilling.personStatus.length : 0,
			sistOppdatert: props.bestilling.sistOppdatert,
			isOpen: true
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
		const bestillingId = this.props.bestilling.id

		try {
			const { data } = await DollyApi.getBestillingStatus(bestillingId)
			if (data.ferdig) {
				this.stopPolling()
			}

			console.log(data, 'data fra tpsf')

			this.updateStatus(data)
		} catch (error) {
			console.log('error', error)
		}
	}

	updateStatus = data => {
		// Setter alltid status til IKKE FERDIG, sånn at vi kan vise
		// en kort melding som sier at prosessen er ferdig
		let newState = {
			ferdig: false,
			antallKlare: data.personStatus.length,
			sistOppdatert: data.sistOppdatert
		}
		this.setState(newState)

		if (data.ferdig) {
			setTimeout(() => {
				// Update groups
				this.props.onGroupUpdate() // state.ferdig = true
				this.props.setBestillingStatus(data.id, { ...data, ny: true })
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

	_onCloseMiljoeStatus = bestillingStatusObj => {
		this.setState({ isOpen: false })
		this.props.setBestillingStatus(bestillingStatusObj.id, { ...bestillingStatusObj, ny: false })
	}

	render() {
		const { bestillingStatusObj, miljoeStatusObj } = this.props

		if (
			(this.state.ferdig && !bestillingStatusObj) ||
			!this.state.isOpen ||
			(bestillingStatusObj && !bestillingStatusObj.ny)
		)
			return null

		const status = this.calculateStatus()
		return (
			<div className="bestilling-status">
				{!this.state.ferdig && <BestillingProgress status={status} />}
				{bestillingStatusObj &&
					bestillingStatusObj.ny && (
						<MiljoeStatus
							miljoeStatusObj={miljoeStatusObj}
							onCloseButton={() => this._onCloseMiljoeStatus(bestillingStatusObj)}
						/>
					)}
			</div>
		)
	}
}
