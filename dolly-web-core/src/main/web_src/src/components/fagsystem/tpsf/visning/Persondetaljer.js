import React from 'react'
import Formatters from '~/utils/DataFormatter'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'

const Persondetaljer = ({ tpsfData, tpsfKriterier }) => {
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
		sivilstand,
		spesreg,
		utenFastBopel,
		gtType,
		gtVerdi,
		tknr,
		tknavn,
		egenAnsattDatoFom
	} = tpsfData

	return (
		<div className="person-details-block">
			<h3>Persondetaljer</h3>
			<div className="person-info-block">
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
				{sivilstand && (
					<div className="person-info-content">
						<h4>Sivilstand</h4>
						<KodeverkValueConnector apiKodeverkId="Sivilstander" value={sivilstand} />
					</div>
				)}
				{spesreg && (
					<div className="person-info-content">
						<h4>Diskresjonskoder</h4>
						<span>
							<KodeverkValueConnector apiKodeverkId="Diskresjonskoder" value={spesreg} />
						</span>
					</div>
				)}
				{tpsfKriterier.utenFastBopel && (
					<div className="person-info-content">
						<h4>Uten fast bopel</h4>
						<span>{Formatters.oversettBoolean(utenFastBopel)}</span>
					</div>
				)}
				{gtType && gtVerdi && (
					<div className="person-info-content">
						<h4>Geo. tilhør.</h4>
						<span>
							<KodeverkValueConnector
								apiKodeverkId={Formatters.gtApiKodeverkId(gtType)}
								extraLabel={Formatters.gtTypeLabel(gtType)}
								value={gtVerdi}
							/>
						</span>
					</div>
				)}
				{tknr && (
					<div className="person-info-content">
						<h4>TK-nummer</h4>
						<span>{tknavn ? `${tknr} - ${tknavn}` : tknr}</span>
					</div>
				)}
				{egenAnsattDatoFom && (
					<div className="person-info-content">
						<h4>Egenansatt</h4>
						<span>JA</span>
					</div>
				)}
			</div>
		</div>
	)
}

export default Persondetaljer
