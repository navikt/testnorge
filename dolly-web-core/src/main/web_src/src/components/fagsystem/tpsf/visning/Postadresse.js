import React from 'react'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'

const Postadresse = ({ postadresse }) => {
	if (!postadresse) return false

	const { postLinje1, postLinje2, postLinje3, postLand } = postadresse

	return (
		<div className="person-details-block">
			<h3>Postadresse</h3>
			<div className="person-info-block">
				<div className="person-info-content">
					<h4>Adresse</h4>
					<div>{postLinje1}</div>
					<div>{postLinje2}</div>
					<div>{postLinje3}</div>
				</div>
				<div className="person-info-content">
					<h4>Land</h4>
					<span>
						<KodeverkValueConnector apiKodeverkId="Landkoder" value={postLand} />
					</span>
				</div>
			</div>
		</div>
	)
}

export default Postadresse
