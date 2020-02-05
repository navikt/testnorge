import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'

import './LinkButton.less'

export default class LinkButton extends PureComponent {
	static propTypes = {
		text: PropTypes.string,
		preventDefault: PropTypes.bool
	}

	static defaultProps = {
		text: '',
		preventDefault: true
	}

	click = (event, ...rest) => {
		if (this.props.preventDefault) event.preventDefault()
		this.props.onClick(event, ...rest)
	}

	render() {
		return (
			<a href="#" className="dolly-link-button" onClick={this.click}>
				{this.props.text}
			</a>
		)
	}
}
