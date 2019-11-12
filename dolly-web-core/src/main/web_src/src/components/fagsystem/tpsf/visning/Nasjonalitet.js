import React from 'react'
import Formatters from '~/utils/DataFormatter'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'

const Nasjonalitet = ({ tpsfData, tpsfKriterier }) => {
	const {
		statsborgerskap,
		statsborgerskapRegdato,
		sprakKode,
		innvandretFraLand,
		innvandretFraLandFlyttedato,
		utvandretTilLand,
		utvandretTilLandFlyttedato
	} = tpsfData

	const innvandretFraLandBestilt = tpsfKriterier.innvandretFraLand

	return (
		<div className="person-details-block">
			<h3>Nasjonalitet</h3>
			<div className="person-info-block">
				<div className="person-info-content">
					<h4>Statsborgerskap</h4>
					<span>
						<KodeverkValueConnector apiKodeverkId="Landkoder" value={statsborgerskap} />
					</span>
				</div>
				<div className="person-info-content">
					<h4>Statsborgerskap fra</h4>
					<span>{Formatters.formatDate(statsborgerskapRegdato)}</span>
				</div>
				<div className="person-info-content">
					<h4>Språk</h4>
					<span>
						<KodeverkValueConnector apiKodeverkId="Språk" value={sprakKode} />
					</span>
				</div>
				{innvandretFraLandBestilt && (
					<div className="person-info-content">
						<h4>Innvandret fra land</h4>
						<span>
							<KodeverkValueConnector apiKodeverkId="Landkoder" value={innvandretFraLand} />
						</span>
					</div>
				)}
				{innvandretFraLandBestilt && (
					<div className="person-info-content">
						<h4>Innvandret dato</h4>
						<span>{Formatters.formatDate(innvandretFraLandFlyttedato)}</span>
					</div>
				)}
				{utvandretTilLand && (
					<div className="person-info-content">
						<h4>Utvandret til land</h4>
						<span>
							<KodeverkValueConnector apiKodeverkId="Landkoder" value={utvandretTilLand} />
						</span>
					</div>
				)}
				{utvandretTilLandFlyttedato && (
					<div className="person-info-content">
						<h4>Utvandret dato</h4>
						<span>{Formatters.formatDate(utvandretTilLandFlyttedato)}</span>
					</div>
				)}
			</div>
		</div>
	)
}

export default Nasjonalitet
