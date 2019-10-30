import React from 'react'
import { useSelector } from 'react-redux'
import '~/pages/gruppe/PersonDetaljer/PersonDetaljer.less' // flytte denne
import '~/components/personInfoBlock/personInfoBlock.less' //flytte denne
// import DataMapper from '~/service/dataMapper'
import KodeverkValueConnector from '~/components/fields/KodeverkValue/KodeverkValueConnector'
import StaticValue from '~/components/fields/StaticValue/StaticValue'
import Formatters from '~/utils/DataFormatter'

export default function TpsfVisning(props) {
	console.log('props :', props)
	const data = useSelector(state => state)
	console.log('data :', data)
	const tpsfData = data.testbruker.items.tpsf.find(({ ident }) => ident === props.personId)
	console.log('tpsfData :', tpsfData)

	// Sjekk først om data/tpsfData finnes??
	if (!tpsfData) return null

	return (
		// miljøer her???
		<div>
			<div className="person-details_content">
				{/* PERSONDETALJER */}
				<h3 className="flexbox--align-center">Persondetaljer</h3>
				<div className="person-info-block_content">
					<div className="static-value">
						<h4 className="static-value">{tpsfData.identtype}</h4>
						<span>{tpsfData.ident}</span>
					</div>
					<div className="static-value">
						<h4>Fornavn</h4>
						<span>{tpsfData.fornavn}</span>
					</div>
					{tpsfData.mellomnavn && (
						<div className="static-value">
							<h4>Mellomnavn</h4>
							<span>{tpsfData.mellomnavn}</span>
						</div>
					)}
					<div className="static-value">
						<h4>Etternavn</h4>
						<span>{tpsfData.etternavn}</span>
					</div>
					<div className="static-value">
						<h4>Kjønn</h4>
						<span>{Formatters.kjonnToString(tpsfData.kjonn)}</span>
					</div>
					<div className="static-value">
						<h4>Alder</h4>
						<span>{tpsfData.alder}</span>
					</div>
					<div className="static-value">
						<h4>Personstatus</h4>
						<span>{tpsfData.personStatus}</span>
						{/* Formatter med apiKodeverkId */}
					</div>
					<div className="static-value">
						<h4>Savnet siden</h4>
						<span>{Formatters.formatDate(tpsfData.forsvunnetDato)}</span>
					</div>
					<div className="static-value">
						<h4>Sivilstand</h4>
						<span>{tpsfData.sivilstand}</span>
					</div>
					<div className="static-value">
						<h4>Diskresjonskoder</h4>
						<span>{tpsfData.spesreg}</span>
					</div>
					<div className="static-value">
						<h4>Uten fast bopel</h4>
						<span>{Formatters.oversettBoolean(tpsfData.utenFastBopel)}</span>
					</div>
					<div className="static-value">
						<h4>Geo. tilhør.</h4>
						<span>{tpsfData.gtVerdi}</span>
						{/* Formatter med apiKodeverkId + extraLabel */}
					</div>
					<div className="static-value">
						<h4>TK-nummer</h4>
						<span>{tpsfData.tknavn ? `${tpsfData.tknr} - ${tpsfData.tknavn}` : tpsfData.tknr}</span>
					</div>
					<div className="static-value">
						<h4>Egenansatt</h4>
						<span>{tpsfData.egenAnsattDatoFom && 'JA'}</span>
					</div>
				</div>
			</div>

			{/* NASJONALITET */}
			<div className="person-details_content">
				<h3 className="flexbox--align-center">Nasjonalitet</h3>
				<div className="person-info-block_content">
					<div className="static-value">
						<h4>Statsborgerskap</h4>
						<span>{tpsfData.statsborgerskap}</span>
					</div>
					<div className="static-value">
						<h4>Statsborgerskap fra</h4>
						<span>{Formatters.formatDate(tpsfData.statsborgerskapRegdato)}</span>
					</div>
					<div className="static-value">
						<h4>Språk</h4>
						<span>{tpsfData.sprakKode}</span>
					</div>
					<div className="static-value">
						<h4>Innvandret fra land</h4>
						<span>{tpsfData.innvandretFraLand}</span>
					</div>
					<div className="static-value">
						<h4>Innvandret dato</h4>
						<span>{Formatters.formatDate(tpsfData.innvandretFraLandFlyttedato)}</span>
					</div>
					<div className="static-value">
						<h4>Utvandret til land</h4>
						<span>{tpsfData.utvandretTilLand}</span>
					</div>
					<div className="static-value">
						<h4>Statsborgerskap</h4>
						<span>{Formatters.formatDate(tpsfData.utvandretTilLandFlyttedato)}</span>
					</div>
				</div>
			</div>

			{/* BOADRESSE */}
			<div className="person-details_content">
				<h3 className="flexbox--align-center">Bostedadresse</h3>
				<div className="person-info-block_content">
					<div className="static-value">
						<h4>{Formatters.adressetypeToString(tpsfData.boadresse.adressetype)}</h4>
						{tpsfData.boadresse.adressetype === 'GATE' && (
							<div>{`${tpsfData.boadresse.gateadresse} ${tpsfData.boadresse.husnummer},\n`}</div>
						)}
						{tpsfData.boadresse.adressetype === 'MATR' && (
							<div>{`${tpsfData.boadresse.mellomnavn}, ${tpsfData.boadresse.gardsnr}/${tpsfData.boadresse.bruksnr}/${tpsfData.boadresse.festenr}-${tpsfData.boadresse.undernr},\n`}</div>
						)}
						<div>{`${tpsfData.boadresse.postnr} ${(
							<KodeverkValueConnector extraLabel={tpsfData.boadresse.postnr} />
						)}`}</div>
					</div>
					<div className="static-value">
						<h4>Flyttedato</h4>
						<span>{Formatters.formatDate(tpsfData.boadresse.flyttedato)}</span>
					</div>
				</div>
			</div>

			{/* POSTADRESSE */}
			{tpsfData.postadresse && (
				<div className="person-details_content">
					<h3 className="flexbox--align-center">Postadresse</h3>
					<div className="person-info-block_content">
						<div className="static-value">
							<h4>Adresse</h4>
							<div>{tpsfData.postadresse[0].postLinje1}</div>
							<div>{tpsfData.postadresse[0].postLinje2}</div>
							<div>{tpsfData.postadresse[0].postLinje3}</div>
						</div>
						<div className="static-value">
							<h4>Land</h4>
							<span>{tpsfData.postadresse[0].postLand}</span>
						</div>
					</div>
				</div>
			)}

			{/* IDENTHISTORIKK */}
			{tpsfData.identHistorikk && (
				<div className="person-details_content">
					<h3 className="flexbox--align-center">Identhistorikk</h3>
					{/* <div className="static-value">
					<h4>Boadresse</h4>
					<span>{`${tpsfData[2].data[1].value} ${tpsfData[2].data[2].value}\n`}</span>
					<span>{`${tpsfData[2].data[8].value} STED`}</span>
				</div> */}
				</div>
			)}

			{/* RELASJONER */}
			{tpsfData.relasjoner && (
				<div className="person-details_content">
					<h3 className="flexbox--align-center">Relasjoner</h3>
					{/* <div className="static-value">
					<h4>Boadresse</h4>
					<span>{`${tpsfData[2].data[1].value} ${tpsfData[2].data[2].value}\n`}</span>
					<span>{`${tpsfData[2].data[8].value} STED`}</span>
				</div> */}
				</div>
			)}
		</div>
	)
}

// import React from 'react'
// import { useSelector } from 'react-redux'

// export const CounterComponent = () => {
// 	const counter = useSelector(state => state.counter)
// 	return <div>{counter}</div>
// }
