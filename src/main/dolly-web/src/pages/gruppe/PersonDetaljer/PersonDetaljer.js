import React, { PureComponent, Fragment } from 'react'
import PropTypes from 'prop-types'
import PersonInfoBlock from '~/components/personInfoBlock/PersonInfoBlock'

import './PersonDetaljer.less'

export default class PersonDetaljer extends PureComponent {
	static propTypes = {}

	componentDidMount() {
		this.props.getSigrunTestbruker()
		this.props.getKrrTestbruker()
	}

	render() {
		const { personData } = this.props

		if (!personData) return null

		return (
			<div className="person-details">
				{personData.map((i, idx) => {
					return (
						// Ikke vis header uten data
						i.data.length > 0 && (
							<div key={idx} className="person-details_content">
								<h3>{i.header}</h3>
								<PersonInfoBlock data={i.data} multiple={i.multiple} />
							</div>
						)
					)
				})}
			</div>
		)
	}
}
