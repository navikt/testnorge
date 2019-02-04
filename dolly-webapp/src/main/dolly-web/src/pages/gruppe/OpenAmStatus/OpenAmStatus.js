import React, { Component, Fragment } from 'react'
import './OpenAmStatus.less'
import Button from '~/components/button/Button'

export default class OpenAmStatus extends Component {
	state = {
		isOpen: true
	}

	render() {
		if (!this.state.isOpen) return null
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
	}

	_onCloseButton = () => {
		this.setState({ isOpen: false })
	}

	_renderJiraLinks = () => {
		const { responses } = this.props
		const jiraArray = []

		responses.forEach(response => {
			response.status.forEach(status => {
				jiraArray.push(status.message)
			})
		})

		const linkArray = jiraArray.map((link, i) => {
			return (
				<Fragment key={i}>
					<a href={link} target="_blank">
						{link.substring(28, link.length)}
					</a>
					{i !== jiraArray.length - 1 && <p>, </p>}
				</Fragment>
			)
		})

		return linkArray
	}
}
