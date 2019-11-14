import React from 'react'
import Formatters from '~/utils/DataFormatter'

const Identhistorikk = ({ identhistorikk }) => {
	if (!identhistorikk) return false

	return (
		<div className="person-details-block">
			<h3>Identhistorikk</h3>
			{identhistorikk.map((alias, i, arr) => {
				const { identtype, ident, kjonn } = alias.aliasPerson
				let className = 'person-info-block'
				if (i !== arr.length - 1) className = 'person-info-block_bottomborder'
				return (
					<div className={className} key={i}>
						<div className="person-info-content_small">
							<span>{`#${i + 1}`}</span>
						</div>
						<div className="person-info-content">
							<h4>Identtype</h4>
							<span>{identtype}</span>
						</div>
						<div className="person-info-content">
							<h4>{identtype}</h4>
							<span>{ident}</span>
						</div>
						<div className="person-info-content">
							<h4>Kjønn</h4>
							<span>{Formatters.kjonnToString(kjonn)}</span>
						</div>
						<div className="person-info-content">
							<h4>Utgått dato</h4>
							<span>{Formatters.formatDate(alias.regdato)}</span>
						</div>
					</div>
				)
			})}
		</div>
	)
}

export default Identhistorikk
