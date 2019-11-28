import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Button from '../Button'

export default class PersonIBrukButton extends PureComponent {
	static propTypes = {
		erIBruk: PropTypes.bool,
		updateIdentAttributter: PropTypes.func
	}

	render() {
		const { erIBruk, updateIdentAttributter } = this.props
		return (
			<Button
				className="flexbox--align-center"
				title={erIBruk ? 'Marker som ikke i bruk' : 'Marker som i bruk'}
				kind={erIBruk ? 'line-version-expanded-button-empty' : 'filled-version-button-empty'}
				onClick={this.settIBruk}
				onMouseEnter={this._handleOnMouseHover}
			/>
		)
	}

	settIBruk = async () => {
		const { erIBruk, updateIdentAttributter, personId, Id } = this.props
		const gruppeId = Id
		const fjern = { ibruk: false, ident: personId }
		const leggtil = { ibruk: true, ident: personId }
		erIBruk
			? await updateIdentAttributter(gruppeId, fjern)
			: await updateIdentAttributter(gruppeId, leggtil)
	}
}
