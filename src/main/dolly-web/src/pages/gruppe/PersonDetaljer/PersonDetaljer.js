import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import PersonInfoBlock from '~/components/personInfoBlock/PersonInfoBlock'

import './PersonDetaljer.less'

export default class PersonDetaljer extends PureComponent {
	static propTypes = {}

	render() {
		const { brukerData } = this.props

		return (
			<div className="person-details">
				{brukerData.map((i, idx) => (
					<div key={idx} className="person-details_content">
						<h4>{i.header}</h4>
						<PersonInfoBlock data={i.data} multiple={i.multiple} />
					</div>
				))}
			</div>
		)
	}
}
