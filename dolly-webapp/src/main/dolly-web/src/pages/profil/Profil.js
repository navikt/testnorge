import React, { Component, Fragment } from 'react'
import PropTypes from 'prop-types'
import Tabs from 'nav-frontend-tabs'
import Overskrift from '~/components/overskrift/Overskrift'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import WideButton from '~/components/button/WideButton/WideButton'
import ContentContainer from '~/components/contentContainer/ContentContainer'

export default class ProfilPage extends Component {
	static propTypes = {
		bruker: PropTypes.object
	}

	render() {
		const { bruker, history } = this.props

		return (
			<Fragment>
				<ContentContainer>
					<Overskrift label="Min profil" />
					<StaticValue header="NAVIDENT" value={bruker.navIdent} />
				</ContentContainer>
				<WideButton iconKind="team" text="Team" onClick={() => this.props.history.push('/team')} />
				{/* <WideButton iconKind="file-new" text="Maler" /> */}
			</Fragment>
		)
	}
}
