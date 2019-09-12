import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Icon from '~/components/icon/Icon'

import './SearchField.less'

// Kopi av NAV, utvides for å kunne legge til ikoner
// TODO: utvide med onChange etc. for å håndtere søk
export default class SearchField extends PureComponent {
	static defaultProps = {
		placeholder: 'Hva leter du etter?'
	}

	onChangeHandler = e => this.props.setSearchText(e.target.value.trim())

	render() {
		return (
			<div className="skjemaelement">
				<div className="searchfield-container">
					<input
						value={this.props.searchText}
						id="searchfield-inputfield"
						type="text"
						placeholder={this.props.placeholder}
						onChange={this.onChangeHandler}
						aria-label="Search"
					/>
					<Icon kind="search" size="20" />
				</div>
				<div aria-live="assertive" role="alert" />
			</div>
		)
	}
}
