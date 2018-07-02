import React, { Component } from 'react'
import PropTypes from 'prop-types'
import cn from 'classnames'
import IconButton from '~/components/fields/IconButton/IconButton'

import './Panel.less'

export default class Panel extends Component {
	static propTypes = {
		forceOpen: PropTypes.bool,
		startOpen: PropTypes.bool,
		heading: PropTypes.node,
		content: PropTypes.node
	}

	static defaultProps = {
		startOpen: false,
		heading: 'Panel'
	}

	state = {
		open: this.props.startOpen
	}

	toggle = event => this.setState({ open: !this.state.open })

	render() {
		const { forceOpen, heading, content, children } = this.props

		const panelIsOpen = forceOpen || this.state.open

		const panelClass = cn('panel', {
			'panel-open': panelIsOpen
		})

		const iconClass = panelIsOpen ? 'chevron-up' : 'chevron-down'

		const renderContent = children ? children : content

		return (
			<div className={panelClass}>
				<div className="panel-heading" onClick={this.toggle}>
					{heading}
					<IconButton kind={iconClass} onClick={this.toggle} />
				</div>
				{panelIsOpen && <div className="panel-content">{renderContent}</div>}
			</div>
		)
	}
}
