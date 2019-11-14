import React from 'react'
import Formatters from '~/utils/DataFormatter'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'
import { relasjonTranslator } from '~/service/dataMapper/Utils'

const Relasjoner = ({ relasjoner, tpsfKriterier }) => {
	if (!relasjoner) return false

	return (
		<div className="person-details-block">
			<h3>Familierelasjoner</h3>
			{relasjoner.map((relasjon, i, arr) => {
				const {
					identtype,
					ident,
					fornavn,
					mellomnavn,
					etternavn,
					kjonn,
					alder,
					doedsdato,
					personStatus,
					forsvunnetDato,
					statsborgerskap,
					statsborgerskapRegdato,
					innvandretFraLand,
					innvandretFraLandFlyttedato,
					utvandretTilLand,
					utvandretTilLandFlyttedato,
					sprakKode,
					sivilstand,
					spesreg,
					egenAnsattDatoFom,
					identHistorikk
				} = relasjon.personRelasjonMed
				const relasjonstype = relasjonTranslator(relasjon.relasjonTypeNavn)

				let className = 'person-info-block'
				if (i !== arr.length - 1) className = 'person-info-block_bottomborder'

				const identHistorikkVisning = (
					<div className="person-info-subItems">
						<h3>Identhistorikk</h3>
						{identHistorikk.map((alias, i) => {
							const { identtype, ident, kjonn } = alias.aliasPerson
							return (
								<div className="subitems" key={i}>
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
									{alias.regdato && (
										<div className="person-info-content">
											<h4>Utgått dato</h4>
											<span>{Formatters.formatDate(alias.regdato)}</span>
										</div>
									)}
								</div>
							)
						})}
					</div>
				)

				return (
					<div key={i}>
						<div className="title-multiple">
							<h3>{relasjonstype}</h3>
						</div>
						<div className={className}>
							<div className="person-info-content">
								<h4>{identtype}</h4>
								<span>{ident}</span>
							</div>
							<div className="person-info-content">
								<h4>Fornavn</h4>
								<span>{fornavn}</span>
							</div>
							{mellomnavn && (
								<div className="person-info-content">
									<h4>Mellomnavn</h4>
									<span>{mellomnavn}</span>
								</div>
							)}
							<div className="person-info-content">
								<h4>Etternavn</h4>
								<span>{etternavn}</span>
							</div>
							<div className="person-info-content">
								<h4>Kjønn</h4>
								<span>{Formatters.kjonnToString(kjonn)}</span>
							</div>
							<div className="person-info-content">
								<h4>Alder</h4>
								<span>{Formatters.formatAlder(alder, doedsdato)}</span>
							</div>
							<div className="person-info-content">
								<h4>Personstatus</h4>
								<span>
									<KodeverkValueConnector apiKodeverkId="Personstatuser" value={personStatus} />
								</span>
							</div>
							{forsvunnetDato && (
								<div className="person-info-content">
									<h4>Savnet siden</h4>
									<span>{Formatters.formatDate(forsvunnetDato)}</span>
								</div>
							)}
							<div className="person-info-content">
								<h4>Statsborgerskap</h4>
								<span>
									<KodeverkValueConnector apiKodeverkId="Landkoder" value={statsborgerskap} />
								</span>
							</div>
							<div className="person-info-content">
								<h4>Statsborgeskap fra</h4>
								<span>{Formatters.formatDate(statsborgerskapRegdato)}</span>
							</div>
							{innvandretFraLand &&
								((relasjonstype === 'Barn' && tpsfKriterier.relasjoner.barn[i].innvandretFraLand) ||
									(relasjonstype === 'Partner' &&
										tpsfKriterier.relasjoner.partner.innvandretFraLand)) && (
									<div className="person-info-content">
										<h4>Innvandret fra land</h4>
										<span>
											<KodeverkValueConnector apiKodeverkId="Landkoder" value={innvandretFraLand} />
										</span>
									</div>
								)}
							{innvandretFraLand &&
								((relasjonstype === 'Barn' &&
									tpsfKriterier.relasjoner.barn[i].innvandretFraLandFlyttedato) ||
									(relasjonstype === 'Partner' &&
										tpsfKriterier.relasjoner.partner.innvandretFraLandFlyttedato)) && (
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
							<div className="person-info-content">
								<h4>Språk</h4>
								<span>
									<KodeverkValueConnector apiKodeverkId="Språk" value={sprakKode} />
								</span>
							</div>
							<div className="person-info-content">
								<h4>Sivilstand</h4>
								<span>
									<KodeverkValueConnector apiKodeverkId="Sivilstander" value={sivilstand} />
								</span>
							</div>
							{spesreg && (
								<div className="person-info-content">
									<h4>Diskresjonskoder</h4>
									<span>
										<KodeverkValueConnector apiKodeverkId="Diskresjonskoder" value={spesreg} />
									</span>
								</div>
							)}
							{egenAnsattDatoFom && (
								<div className="person-info-content">
									<h4>Egenansatt</h4>
									<span>JA</span>
								</div>
							)}
							{identHistorikk && identHistorikk.length > 0 && identHistorikkVisning}
						</div>
					</div>
				)
			})}
		</div>
	)
}

export default Relasjoner
