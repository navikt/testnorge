import React, { Component } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import IconButton from '~/components/fields/IconButton/IconButton'

import './Panel.less'

export default class Panel extends Component {
	static propTypes = {
		forceOpen: PropTypes.bool,
		startOpen: PropTypes.bool,
		heading: PropTypes.node.isRequired,
		content: PropTypes.node.isRequired
	}

	state = {
		open: Boolean(this.props.startOpen)
	}

	toggle = () => this.setState({ open: !this.state.open })

	render() {
		const { forceOpen, heading, content } = this.props

		const panelIsOpen = forceOpen || this.state.open

		const panelClass = cn('panel', {
			'panel-open': panelIsOpen
		})

		return (
			<div className={panelClass}>
				<div className="panel-heading">
					{heading}
					<IconButton
						iconName={panelIsOpen ? 'chevron-up' : 'chevron-down'}
						onClick={this.toggle}
					/>
				</div>
				{panelIsOpen && <div className="panel-content">{content}</div>}
			</div>
		)
	}
}
