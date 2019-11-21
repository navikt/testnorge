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
				kind={erIBruk ? 'star-filled' : 'star'} //! FINN ET BEDRE ICON F.EKS. EN BOX
				onClick={this.settIBruk}
				onMouseEnter={this._handleOnMouseHover}
			/>
		)
	}

	settIBruk = async () => {
		const { erIBruk, updateIdentAttributter, personId } = this.props
		const gruppeID = 24 //! MÅ HENTE RIKTIG GRUPPEID
		const fjern = { ibruk: false, ident: personId }
		const leggtil = { ibruk: true, ident: personId }
		erIBruk
			? await updateIdentAttributter(gruppeID, fjern)
			: await updateIdentAttributter(gruppeID, leggtil)
	}
}
