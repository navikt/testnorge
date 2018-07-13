import React, { Component } from 'react'
import PropTypes from 'prop-types'
import Tabs from 'nav-frontend-tabs'
import Overskrift from '~/components/overskrift/Overskrift'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Button from '~/components/button/Button'

import './Profil.less'

export default class ProfilPage extends Component {
	static propTypes = {
		bruker: PropTypes.object
	}

	render() {
		const { bruker, history } = this.props
		console.log(this.props)

		return (
			<div className="profil-container">
				<Overskrift label="Min profil" />
				<StaticValue header="NAVIDENT" value={bruker.navIdent} />
				<StaticValue header="ROLLE" value={bruker.rolle} />

				<Button onClick={() => history.push('/team')}>Teams</Button>
			</div>
		)
	}
}
