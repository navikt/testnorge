import React, { PureComponent } from 'react'
import Icon from '~/components/icon/Icon'
import Tooltip from 'rc-tooltip'

import './FixedButton.less'

class FixedButton extends PureComponent {
	render() {
		return (
			<Tooltip overlay={<span style={{ fontSize: 14 }}>Opprett team</span>} placement="top">
				<button className="fixed-button" onClick={this.props.onClick}>
					<Icon size={36} kind="add-circle" />
				</button>
			</Tooltip>
		)
	}
}

export default FixedButton
