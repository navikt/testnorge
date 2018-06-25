import React, { PureComponent } from 'react'
import PropTypes from 'prop-types'
import IconButton from '~/components/fields/IconButton/IconButton'

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
							Personinfo <IconButton iconName="times-circle-o" />
						</span>
						<ul>
							<li>
								<span>
									Kjønn <IconButton iconName="times-circle-o" />
								</span>
							</li>
							<li>
								<span>
									Født etter <IconButton iconName="times-circle-o" />
								</span>
							</li>
							<li>
								<span>
									Født før <IconButton iconName="times-circle-o" />
								</span>
							</li>
						</ul>
					</li>
					<li>
						<span>
							Adresser <IconButton iconName="times-circle-o" />
						</span>
					</li>
					<li>
						<span>
							Arbeidsforhold <IconButton iconName="times-circle-o" />
						</span>
					</li>
				</ul>
			</div>
		)
	}
}
