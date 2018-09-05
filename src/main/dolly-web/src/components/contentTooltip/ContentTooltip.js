import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import Tooltip from 'rc-tooltip'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import Icon from '~/components/icon/Icon'
import Button from '~/components/button/Button'
import DollyHjelpetekst from '~/utils/DollyHjelpetekst'

import './ContentTooltip.less'

export default class ContentTooltip extends PureComponent {
	render() {
		//TODO: STØTTE ANDRE TYPER CONTENT I TOOLTIP
		return (
			<HjelpeTekst className="content-tooltip" id="hjelpetekst" type="under-hoyre">
				<DollyHjelpetekst />
			</HjelpeTekst>
		)
	}
}
