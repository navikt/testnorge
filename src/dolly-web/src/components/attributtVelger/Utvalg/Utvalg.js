import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import IconButton from '~/components/button/IconButton/IconButton'

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
							Personinfo <IconButton kind="remove-circle" />
						</span>
						<ul>
							<li>
								<span>
									Kjønn <IconButton kind="remove-circle" />
								</span>
							</li>
							<li>
								<span>
									Født etter <IconButton kind="remove-circle" />
								</span>
							</li>
							<li>
								<span>
									Født før <IconButton kind="remove-circle" />
								</span>
							</li>
						</ul>
					</li>
					<li>
						<span>
							Adresser <IconButton kind="remove-circle" />
						</span>
					</li>
					<li>
						<span>
							Arbeidsforhold <IconButton kind="remove-circle" />
						</span>
					</li>
				</ul>
			</div>
		)
	}
}
