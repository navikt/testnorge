import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Button from '~/components/button/Button'

import './Utvalg.less'

export default class Utvalg extends PureComponent {
	static propTypes = {}

	render() {
		const { selectedTypes, attributter } = this.props
		// TODO: Legg til remove button
		return (
			<div className="utvalg">
				<h2>Du har lagt til følgende egenskaper:</h2>

				<ul>
					<li>
						<span>Personinformasjon</span>
						<ul>
							{attributter.personinformasjon.map(group => {
								return group.items.map(item => {
									return (
										Boolean(selectedTypes[item.id]) && (
											<li key={item.id}>
												<span>{item.label}</span>
											</li>
										)
									)
								})
							})}
						</ul>
					</li>
				</ul>
			</div>
		)
	}
}
