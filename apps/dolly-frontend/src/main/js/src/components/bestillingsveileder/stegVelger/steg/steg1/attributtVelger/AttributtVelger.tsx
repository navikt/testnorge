import { Utvalg } from './utvalg/Utvalg'

import './AttributtVelger.less'

export const AttributtVelger = ({ checked, children }) => {
	return (
		<div className="attributt-velger">
			<div className="flexbox">
				<div className="attributt-velger_panels">{children}</div>
				<Utvalg checked={checked} />
			</div>
		</div>
	)
}
