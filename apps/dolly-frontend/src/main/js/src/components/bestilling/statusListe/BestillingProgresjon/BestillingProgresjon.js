import React, { Fragment, PureComponent } from 'react'
import PropTypes from 'prop-types'
import { DollyApi } from '~/service/Api'
import Loading from '~/components/ui/loading/Loading'
import { Line } from 'rc-progress'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Icon from '~/components/ui/icon/Icon'

import './BestillingProgresjon.less'

export default class BestillingProgresjon extends PureComponent {
	static propTypes = {
		bestilling: PropTypes.object.isRequired,
	}

	constructor(props) {
		super(props)

		this.PULL_INTERVAL = 1000
		this.PULL_INTERVAL_ORG = 5000
		this.TIMEOUT_BEFORE_HIDE = 2000
		this.TIME_BEFORE_WARNING_MESSAGE = 120

		this.state = {
			ferdig: props.bestilling.ferdig,
			antallLevert: props.bestilling.antallLevert,
			failureIntervalCounter: 0,
			failed: false,
			sistOppdatert: props.bestilling.sistOppdatert,
			orgStatus: null,
		}
	}

	componentDidMount() {
		if (!this.state.ferdig && !this.props.bestilling.organisasjonNummer) {
			this.interval = setInterval(() => this.getBestillingStatus(), this.PULL_INTERVAL)
		}
		if (
			!this.state.ferdig &&
			(sessionStorage.getItem('organisasjon_bestilling') ||
				this.props.bestilling.organisasjonNummer)
		) {
			this.interval = setInterval(
				() => this.getOrganisasjonBestillingStatus(),
				this.PULL_INTERVAL_ORG
			)
		}
	}

	componentWillUnmount() {
		this.stopPolling()
	}

	stopPolling = () => clearInterval(this.interval)

	getOrganisasjonBestillingStatus = async () => {
		const bestillingId = this.props.bestilling?.id

		try {
			const { data } = await DollyApi.getOrganisasjonBestillingStatus(bestillingId)

			console.log('data: ', data) //TODO - SLETT MEG

			if (!data?.message?.includes('Status ikke funnet')) {
				sessionStorage.clear()
			}

			if (data.ferdig) {
				this.stopPolling()
			}
			this.updateOrgStatus(data)
		} catch (error) {
			console.error(error)
		}
	}

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

	updateOrgStatus = (data) => {
		// Setter alltid status til IKKE FERDIG, sånn at vi kan vise
		// en kort melding som sier at prosessen er ferdig
		const newState = {
			ferdig: false,
			antallLevert: data.antallLevert,
			sistOppdatert: data.sistOppdatert,
			orgStatus: data.status?.[0]?.statuser[0]?.detaljert?.[0]?.detaljertStatus,
		}
		this.setState(newState)

		if (data.ferdig) {
			setTimeout(async () => {
				await this.props.getBestillinger() // state.ferdig = true
				await this.props.getOrganisasjoner(this.props.brukerId)
			}, this.TIMEOUT_BEFORE_HIDE)
		}
	}

	updateStatus = (data) => {
		// Setter alltid status til IKKE FERDIG, sånn at vi kan vise
		// en kort melding som sier at prosessen er ferdig
		const newState = {
			ferdig: false,
			antallLevert: data.antallLevert,
			sistOppdatert: data.sistOppdatert,
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

	harBestillingFeilet = (sistOppdatert) => {
		const liveTimeStamp = new Date(sistOppdatert).getTime()
		const oldTimeStamp = new Date(this.state.sistOppdatert).getTime()

		if (liveTimeStamp === oldTimeStamp) {
			this.setState({ failureIntervalCounter: this.state.failureIntervalCounter + 1 })
			// Etter et bestemt intervall uten update av timestamp, setter bestilling til failed
			if (this.state.failureIntervalCounter === this.TIME_BEFORE_WARNING_MESSAGE) {
				this.setState({ failed: true })
			}
		} else {
			this.setState({ sistOppdatert: sistOppdatert, failureIntervalCounter: 0, failed: false })
		}
	}

	calculateStatus = () => {
		const total = this.props.bestilling.organisasjonNummer ? 1 : this.props.bestilling.antallIdenter
		const sykemelding =
			this.props.bestilling.bestilling.sykemelding != null &&
			this.props.bestilling.bestilling.sykemelding.syntSykemelding != null
		const organisasjon = this.props.bestilling.hasOwnProperty('organisasjonNummer')
		const { antallLevert } = this.state

		// Percent
		let percent = (100 / total) * antallLevert
		let text = `Opprettet ${antallLevert} av ${total}`

		// To indicate progress hvis ingenting har skjedd enda
		if (percent === 0) percent += 10

		if (antallLevert === total) text = `Ferdigstiller bestilling`

		const aktivBestilling = sykemelding
			? 'AKTIV BESTILLING (Syntetisert sykemelding behandler mye data og kan derfor ta litt tid)'
			: organisasjon
			? `AKTIV BESTILLING (${
					this.state.orgStatus
						? this.state.orgStatus
						: 'Bestillingen tar opptil flere minutter per valgte miljø'
			  })`
			: 'AKTIV BESTILLING'
		const title = percent === 100 ? 'FERDIG' : aktivBestilling

		return {
			percent,
			title,
			text,
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
								Dette tar lengre tid enn forventet. Hvis bestillingen er kompleks kan du gi Dolly
								litt mer tid før du eventuelt avbryter.
							</h5>
						</div>
						<NavButton type="fare" onClick={this._onCancelBtn}>
							AVBRYT BESTILLING
						</NavButton>
					</div>
				)}
			</Fragment>
		)
	}
}
