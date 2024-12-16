import * as _ from 'lodash-es'

import './historikk.less'

export const Historikk = ({ component, data, propName = 'data', ...restProps }) => {
	if (!_.isArray(data)) {
		return null
	}

	const Main = component

	// Hvis det kun finnes en
	if (data.length <= 1) return <Main {...{ [propName]: data[0], ...restProps }} />

	return (
		<div className="med-historikk">
			<Main {...{ [propName]: data[0], ...restProps }} />
			<div className="med-historikk-blokk">
				<h5>Historikk</h5>
				{_.drop(data).map((element, idx) => (
					<div key={idx} className="med-historikk-content">
						<Main {...{ [propName]: element, ...restProps }} />
					</div>
				))}
			</div>
		</div>
	)
}
