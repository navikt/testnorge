import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import { DollyApi } from '~/service/Api'
import Loading from '~/components/loading/Loading'
import BestillingProgress from './BestillingProgress/BestillingProgress'
import MiljoeStatus from './MiljoeStatus/MiljoeStatus'
import './BestillingStatus.less'
import _find from 'lodash/find'
import ContentContainer from '~/components/contentContainer/ContentContainer'

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
			failureIntervalCounter: 0,
			failed: false,
			sistOppdatert: props.bestilling.sistOppdatert,
			isOpen: true,
			showCancelLoadingMsg: false
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

		const liveTimeStamp = new Date(data.sistOppdatert).getTime()
		const oldTimeStamp = new Date(this.state.sistOppdatert).getTime()

		if (liveTimeStamp == oldTimeStamp) {
			this.setState({ failureIntervalCounter: (this.state.failureIntervalCounter += 1) })
			// Etter et bestemt intervall uten update av timestamp, setter bestilling til failed
			this.state.failureIntervalCounter == 60 && this.setState({ failed: true })
		} else {
			this.setState({ sistOppdatert: data.sistOppdatert, failureIntervalCounter: 0, failed: false })
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

		if (antallKlare === total) text = `Ferdigstiller bestilling`

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

	_onCancelBtn = () => {
		this.setState({ ferdig: true, showCancelLoadingMsg: true }, () => {
			this.props.cancelBestilling()
			this.stopPolling()
		})
	}

	render() {
		const {
			bestillingStatusObj,
			miljoeStatusObj,
			isCanceling,
			cancelBestilling,
			bestilling
		} = this.props

		console.log(bestilling)
		if (isCanceling && this.state.showCancelLoadingMsg) {
			return (
				<ContentContainer className="loading-content-container">
					<Loading label="AVBRYTER BESTILLING" />
				</ContentContainer>
			)
		}

		if (
			(this.state.ferdig && !bestillingStatusObj) ||
			!this.state.isOpen ||
			(bestillingStatusObj && !bestillingStatusObj.ny)
		)
			return null

		const status = this.calculateStatus()
		return (
			<div className="bestilling-status">
				{!this.state.ferdig ? (
					<BestillingProgress
						status={status}
						failed={this.state.failed}
						cancelBestilling={this._onCancelBtn}
					/>
				) : (
					bestillingStatusObj &&
					bestillingStatusObj.ny && (
						<MiljoeStatus
							miljoeStatusObj={miljoeStatusObj}
							onCloseButton={() => this._onCloseMiljoeStatus(bestillingStatusObj)}
						/>
					)
				)}
			</div>
		)
	}
}
