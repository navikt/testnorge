import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Icon from '~/components/icon/Icon'

import './WideButton.less'

export default class WideButton extends PureComponent {
	static propTypes = {
		iconKind: PropTypes.string.isRequired,
		text: PropTypes.string.isRequired
	}

	render() {
		const { iconKind, text } = this.props
		return (
			<button className="wide-button">
				<span className="wide-button_icon">
					<Icon kind={iconKind} size={30} />
				</span>

				<span className="wide-button_label">
					<span className="wide-button_label_text">{text}</span>
					<span className="wide-button_label_icon">
						<Icon kind="chevron-right" />
					</span>
				</span>
			</button>
		)
	}
}
