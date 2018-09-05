import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Icon from '~/components/icon/Icon'

import './SearchField.less'

// Kopi av NAV, utvides for å kunne legge til ikoner
// TODO: utvide med onChange etc. for å håndtere søk
export default class SearchField extends PureComponent {
	render() {
		return (
			<div className="skjemaelement">
				<div className="searchfield-container">
					<input id="searchfield-inputfield" type="text" placeholder="Hva leter du etter?" />
					<Icon kind="search" size="20" />
				</div>
				<div aria-live="assertive" role="alert" />
			</div>
		)
	}
}
