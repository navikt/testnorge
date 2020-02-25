import React from 'react'
import _isArray from 'lodash/isArray'
import _drop from 'lodash/drop'

import './historikk.less'

export const Historikk = ({ component, data, propName = 'data', ...restProps }) => {
	if (!_isArray(data)) return false

	const main = React.createElement(component, { [propName]: data[0], ...restProps })

	// Hvis det kun finnes en
	if (data.length <= 1) return main

	return (
		<div className="med-historikk">
			{main}
			<div className="med-historikk-blokk">
				<h5>Historikk</h5>
				{_drop(data).map((element, idx) => (
					<div key={idx} className="med-historikk-content">
						{React.createElement(component, { [propName]: element, ...restProps })}
					</div>
				))}
			</div>
		</div>
	)
}
