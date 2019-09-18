import React, { Component, Fragment } from 'react'
import './OpenAmStatus.less'
import Button from '~/components/ui/button/Button'

export default class OpenAmStatus extends Component {
	state = {
		isOpen: true
	}

	render() {
		const { lukket } = this.props

		if (this.state.isOpen && !lukket) {
			return (
				<div className="open-am-status">
					<div>
						<h3>JIRA-saker opprettet</h3>
						<div className="jira-link">{this._renderJiraLinks()}</div>
						<div>
							Sakene vil bli oppdatert i løpet av 2-3 minutter, og finnes da i kommentarfeltet.
						</div>
					</div>
					<Button kind="remove-circle" onClick={this._onCloseButton} />
				</div>
			)
		} else {
			return (
				<div className="bestilling-detaljer">
					<h3>Jira-lenker</h3>
					<div className={'jira-link'}>{this._renderJiraLinks()}</div>
				</div>
			)
		}
	}

	_onCloseButton = () => {
		this.props.removeNyOpenAmStatus(this.props.id)
		this.setState({ isOpen: false })
	}

	_renderJiraLinks = () => {
		const data = this.props.responses
		const linkArray = data.map((respons, i) => {
			return (
				<Fragment key={i}>
					<a href={respons} target="_blank">
						{respons.substring(28, respons.length)}
					</a>
					{i !== data.length - 1 && <p>, </p>}
				</Fragment>
			)
		})
		return linkArray
	}
}
