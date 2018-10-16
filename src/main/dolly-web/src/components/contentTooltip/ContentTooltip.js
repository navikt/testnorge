import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import Tooltip from 'rc-tooltip'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'

import './ContentTooltip.less'

export default class ContentTooltip extends PureComponent {
	static propTypes = {
		children: PropTypes.node.isRequired
	}

	render() {
		return (
			<HjelpeTekst className="content-tooltip" id="hjelpetekst" type="under-hoyre">
				{this.props.children}
			</HjelpeTekst>
		)
	}
}
