import React from 'react'
import { useSelector } from 'react-redux'
import '~/components/fagsystem/fagsystemVisning/fagsystemVisning.less'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'
import Formatters from '~/utils/DataFormatter'
import { relasjonTranslator } from '~/service/dataMapper/Utils'
import Nasjonalitet from './Nasjonalitet'
import Boadresse from './Boadresse'
import Postadresse from './Postadresse'
import Identhistorikk from './Identhistorikk'

export default function TpsfVisning(props) {
	const data = useSelector(state => state)
	const tpsfData = data.testbruker.items.tpsf.find(({ ident }) => ident === props.personId)

	const bestillingData = data.bestillingStatuser.data.find(
		({ id }) => id === parseInt(props.bestillingId)
	)
	const tpsfKriterier = JSON.parse(bestillingData.tpsfKriterier)

	if (!tpsfData) return null
	console.log('tpsfData :', tpsfData)

	return (
		<div>
			<div className="person-details-block">
				{/* PERSONDETALJER */}
				<h3>Persondetaljer</h3>
				<div className="person-info-block">
					<div className="person-info-content">
						<h4>{tpsfData.identtype}</h4>
						<span>{tpsfData.ident}</span>
					</div>
					<div className="person-info-content">
						<h4>Fornavn</h4>
						<span>{tpsfData.fornavn}</span>
					</div>
					{tpsfData.mellomnavn && (
						<div className="person-info-content">
							<h4>Mellomnavn</h4>
							<span>{tpsfData.mellomnavn}</span>
						</div>
					)}
					<div className="person-info-content">
						<h4>Etternavn</h4>
						<span>{tpsfData.etternavn}</span>
					</div>
					<div className="person-info-content">
						<h4>Kjønn</h4>
						<span>{Formatters.kjonnToString(tpsfData.kjonn)}</span>
					</div>
					<div className="person-info-content">
						<h4>Alder</h4>
						<span>{Formatters.formatAlder(tpsfData.alder, tpsfData.doedsdato)}</span>
					</div>
					<div className="person-info-content">
						<h4>Personstatus</h4>
						<span>
							<KodeverkValueConnector
								apiKodeverkId="Personstatuser"
								value={tpsfData.personStatus}
							/>
						</span>
					</div>
					{tpsfData.forsvunnetDato && (
						<div className="person-info-content">
							<h4>Savnet siden</h4>
							<span>{Formatters.formatDate(tpsfData.forsvunnetDato)}</span>
						</div>
					)}
					{tpsfData.sivilstand && (
						<div className="person-info-content">
							<h4>Sivilstand</h4>
							<KodeverkValueConnector apiKodeverkId="Sivilstander" value={tpsfData.sivilstand} />
						</div>
					)}
					{tpsfData.spesreg && (
						<div className="person-info-content">
							<h4>Diskresjonskoder</h4>
							<span>
								<KodeverkValueConnector apiKodeverkId="Diskresjonskoder" value={tpsfData.spesreg} />
							</span>
						</div>
					)}
					{tpsfKriterier.utenFastBopel && (
						<div className="person-info-content">
							<h4>Uten fast bopel</h4>
							<span>{Formatters.oversettBoolean(tpsfData.utenFastBopel)}</span>
						</div>
					)}
					{tpsfData.gtType && tpsfData.gtVerdi && (
						<div className="person-info-content">
							<h4>Geo. tilhør.</h4>
							<span>
								<KodeverkValueConnector
									apiKodeverkId={Formatters.gtApiKodeverkId(tpsfData.gtType)}
									extraLabel={Formatters.gtTypeLabel(tpsfData.gtType)}
									value={tpsfData.gtVerdi}
								/>
							</span>
						</div>
					)}
					{tpsfData.tknr && (
						<div className="person-info-content">
							<h4>TK-nummer</h4>
							<span>
								{tpsfData.tknavn ? `${tpsfData.tknr} - ${tpsfData.tknavn}` : tpsfData.tknr}
							</span>
						</div>
					)}
					{tpsfData.egenAnsattDatoFom && (
						<div className="person-info-content">
							<h4>Egenansatt</h4>
							<span>JA</span>
						</div>
					)}
				</div>
			</div>

			{/* NASJONALITET */}
			<Nasjonalitet tpsfData={tpsfData} tpsfKriterier={tpsfKriterier} />

			{/* BOADRESSE */}
			<Boadresse boadresse={tpsfData.boadresse} />

			{/* POSTADRESSE */}
			<Postadresse postadresse={tpsfData.postadresse && tpsfData.postadresse[0]} />

			{/* IDENTHISTORIKK */}
			<Identhistorikk identhistorikk={tpsfData.identHistorikk} />

			{/* RELASJONER */}
			{tpsfData.relasjoner && (
				<div className="person-details-block">
					<h3>Familierelasjoner</h3>
					{tpsfData.relasjoner.map((relasjon, i, arr) => {
						let className = 'person-info-block'
						if (i !== arr.length - 1) className = 'person-info-block_bottomborder'
						const relasjonstype = relasjonTranslator(relasjon.relasjonTypeNavn)
						return (
							<div>
								<div className="title-multiple">
									<h3>{relasjonstype}</h3>
								</div>
								<div className={className}>
									<div className="person-info-content">
										<h4>{relasjon.personRelasjonMed.identtype}</h4>
										<span>{relasjon.personRelasjonMed.ident}</span>
									</div>
									<div className="person-info-content">
										<h4>Fornavn</h4>
										<span>{relasjon.personRelasjonMed.fornavn}</span>
									</div>
									{relasjon.personRelasjonMed.mellomnavn && (
										<div className="person-info-content">
											<h4>Mellomnavn</h4>
											<span>{relasjon.personRelasjonMed.mellomnavn}</span>
										</div>
									)}
									<div className="person-info-content">
										<h4>Etternavn</h4>
										<span>{relasjon.personRelasjonMed.etternavn}</span>
									</div>
									<div className="person-info-content">
										<h4>Kjønn</h4>
										<span>{Formatters.kjonnToString(relasjon.personRelasjonMed.kjonn)}</span>
									</div>
									<div className="person-info-content">
										<h4>Alder</h4>
										<span>
											{Formatters.formatAlder(
												relasjon.personRelasjonMed.alder,
												relasjon.personRelasjonMed.doedsdato
											)}
										</span>
									</div>
									<div className="person-info-content">
										<h4>Personstatus</h4>
										<span>
											<KodeverkValueConnector
												apiKodeverkId="Personstatuser"
												value={relasjon.personRelasjonMed.personStatus}
											/>
										</span>
									</div>
									{relasjon.personRelasjonMed.forsvunnetDato && (
										<div className="person-info-content">
											<h4>Savnet siden</h4>
											<span>
												{Formatters.formatDate(relasjon.personRelasjonMed.forsvunnetDato)}
											</span>
										</div>
									)}
									<div className="person-info-content">
										<h4>Statsborgerskap</h4>
										<span>
											<KodeverkValueConnector
												apiKodeverkId="Landkoder"
												value={relasjon.personRelasjonMed.statsborgerskap}
											/>
										</span>
									</div>
									<div className="person-info-content">
										<h4>Statsborgeskap fra</h4>
										<span>
											{Formatters.formatDate(relasjon.personRelasjonMed.statsborgerskapRegdato)}
										</span>
									</div>
									{relasjon.personRelasjonMed.innvandretFraLand &&
										((relasjonstype === 'Barn' &&
											tpsfKriterier.relasjoner.barn[i].innvandretFraLand) ||
											(relasjonstype === 'Partner' &&
												tpsfKriterier.relasjoner.partner.innvandretFraLand)) && (
											<div className="person-info-content">
												<h4>Innvandret fra land</h4>
												<span>
													<KodeverkValueConnector
														apiKodeverkId="Landkoder"
														value={relasjon.personRelasjonMed.innvandretFraLand}
													/>
												</span>
											</div>
										)}
									{relasjon.personRelasjonMed.innvandretFraLand &&
										((relasjonstype === 'Barn' &&
											tpsfKriterier.relasjoner.barn[i].innvandretFraLandFlyttedato) ||
											(relasjonstype === 'Partner' &&
												tpsfKriterier.relasjoner.partner.innvandretFraLandFlyttedato)) && (
											<div className="person-info-content">
												<h4>Innvandret dato</h4>
												<span>
													{Formatters.formatDate(
														relasjon.personRelasjonMed.innvandretFraLandFlyttedato
													)}
												</span>
											</div>
										)}
									{relasjon.personRelasjonMed.utvandretTilLand && (
										<div className="person-info-content">
											<h4>Utvandret til land</h4>
											<span>
												<KodeverkValueConnector
													apiKodeverkId="Landkoder"
													value={relasjon.personRelasjonMed.utvandretTilLand}
												/>
											</span>
										</div>
									)}
									{relasjon.personRelasjonMed.utvandretTilLandFlyttedato && (
										<div className="person-info-content">
											<h4>Utvandret dato</h4>
											<span>
												{Formatters.formatDate(
													relasjon.personRelasjonMed.utvandretTilLandFlyttedato
												)}
											</span>
										</div>
									)}
									<div className="person-info-content">
										<h4>Språk</h4>
										<span>
											<KodeverkValueConnector
												apiKodeverkId="Språk"
												value={relasjon.personRelasjonMed.sprakKode}
											/>
										</span>
									</div>
									<div className="person-info-content">
										<h4>Sivilstand</h4>
										<span>
											<KodeverkValueConnector
												apiKodeverkId="Sivilstander"
												value={relasjon.personRelasjonMed.sivilstand}
											/>
										</span>
									</div>
									{relasjon.personRelasjonMed.spesreg && (
										<div className="person-info-content">
											<h4>Diskresjonskoder</h4>
											<span>
												<KodeverkValueConnector
													apiKodeverkId="Diskresjonskoder"
													value={relasjon.personRelasjonMed.spesreg}
												/>
											</span>
										</div>
									)}
									{relasjon.personRelasjonMed.egenAnsattDatoFom && (
										<div className="person-info-content">
											<h4>Egenansatt</h4>
											<span>JA</span>
										</div>
									)}
									{/* Tilpass denne når bestilling med relasjoner er fikset! */}
									{relasjon.personRelasjonMed.identHistorikk &&
										relasjon.personRelasjonMed.identHistorikk.length > 0 && (
											<div className="person-details-block">
												<h3>Identhistorikk</h3>
												{relasjon.personRelasjonMed.identHistorikk.map((ident, i, arr) => {
													let className = 'person-info-block'
													if (i !== arr.length - 1) className = 'person-info-block_bottomborder'
													return (
														<div className={className}>
															<div className="person-info-content_small">
																<span>{`#${i + 1}`}</span>
															</div>
															<div className="person-info-content">
																<h4>Identtype</h4>
																<span>{ident.aliasPerson.identtype}</span>
															</div>
															<div className="person-info-content">
																<h4>{ident.aliasPerson.identtype}</h4>
																<span>{ident.aliasPerson.ident}</span>
															</div>
															<div className="person-info-content">
																<h4>Kjønn</h4>
																<span>{Formatters.kjonnToString(ident.aliasPerson.kjonn)}</span>
															</div>
															<div className="person-info-content">
																<h4>Utgått dato</h4>
																<span>{Formatters.formatDate(ident.regdato)}</span>
															</div>
														</div>
													)
												})}
											</div>
										)}
								</div>
							</div>
						)
					})}
				</div>
			)}
		</div>
	)
}
