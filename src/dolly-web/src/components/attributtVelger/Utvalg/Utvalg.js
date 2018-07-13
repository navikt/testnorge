import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import Button from '~/components/button/Button'

import './Utvalg.less'

export default class Utvalg extends PureComponent {
	static propTypes = {}

	render() {
		const { utvalg } = this.props

		return (
			<div className="utvalg">
				<h2>Du har lagt til følgende egenskaper:</h2>

				<ul>
					<li>
						<span>
							Personinfo <Button kind="remove-circle" />
						</span>
						<ul>
							<li>
								<span>
									Kjønn <Button kind="remove-circle" />
								</span>
							</li>
							<li>
								<span>
									Født etter <Button kind="remove-circle" />
								</span>
							</li>
							<li>
								<span>
									Født før <Button kind="remove-circle" />
								</span>
							</li>
						</ul>
					</li>
					<li>
						<span>
							Adresser <Button kind="remove-circle" />
						</span>
					</li>
					<li>
						<span>
							Arbeidsforhold <Button kind="remove-circle" />
						</span>
					</li>
				</ul>
			</div>
		)
	}
}
