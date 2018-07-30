import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import PersonInfoBlock from '~/components/personInfoBlock/PersonInfoBlock'

export default class PersonDetaljer extends PureComponent {
	static propTypes = {}

	render() {
		const { brukerData } = this.props

		return (
			<div className="person-details">
				{brukerData.map(i => (
					<Fragment>
						<h4>{i.label}</h4>
						<PersonInfoBlock data={i.data} />
					</Fragment>
				))}
			</div>
		)
	}
}
