import React from 'react'
import './DataVisning.less'
// @ts-ignore
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap_white.css'
import { IdentInfo } from '~/components/fagsystem/pdlf/visning/partials/Identinfo'
import { GeografiskTilknytning } from '~/components/fagsystem/pdlf/visning/partials/GeografiskTilknytning'
import { PdlPersonInfo } from '~/components/fagsystem/pdlf/visning/partials/PdlPersonInfo'
import { PdlNasjonalitet } from '~/components/fagsystem/pdlf/visning/partials/PdlNasjonalitet'
import { PdlFullmakt } from '~/components/fagsystem/pdlf/visning/partials/PdlFullmakt'
import { PdlSikkerhetstiltak } from '~/components/fagsystem/pdlf/visning/partials/PdlSikkerhetstiltak'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { Boadresse } from '~/components/fagsystem/pdlf/visning/partials/Boadresse'
import { Oppholdsadresse } from '~/components/fagsystem/pdlf/visning/partials/Oppholdsadresse'
import { Kontaktadresse } from '~/components/fagsystem/pdlf/visning/partials/Kontaktadresse'
import { Adressebeskyttelse } from '~/components/fagsystem/pdlf/visning/partials/Adressebeskyttelse'

type PdlData = {
	data: Data
}

type Data = {
	hentIdenter: { identer: [Ident] }
	hentPerson: HentPerson
	hentGeografiskTilknytning: object
}

type Ident = {
	gruppe: string
	ident: string
	historisk: boolean
}

type HentPerson = {
	bostedsadresse: Array<BostedData>
	oppholdsadresse: Array<{}>
	kontaktadresse: Array<{}>
	adressebeskyttelse: Array<{}>
	fullmakt: [FullmaktData]
	telefonnummer: Array<TelefonData>
	sikkerhetstiltak: [SikkerhetstiltakData]
}

type BostedData = {
	metadata: {
		historisk: boolean
	}
}

type FullmaktData = {
	gyldigFraOgMed: Date
	gyldigTilOgMed: Date
	motpartsPersonident: string
	motpartsRolle: string
	omraader: []
}

type TelefonData = {
	landskode: string
	nummer: string
	prioritet: number
}

type SikkerhetstiltakData = {
	gyldigFraOgMed: Date
	gyldigTilOgMed: Date
	tiltakstype: string
	beskrivelse: string
	kontaktperson: Kontaktperson
	omraader: []
}

type Kontaktperson = {
	personident: string
	enhet: string
}

export const PdlDataVisning = ({ data }: PdlData) => {
	const { hentPerson, hentIdenter, hentGeografiskTilknytning } = data
	const {
		telefonnummer,
		bostedsadresse,
		oppholdsadresse,
		kontaktadresse,
		adressebeskyttelse,
		fullmakt,
		sikkerhetstiltak,
	} = hentPerson

	const getPersonInfo = () => {
		return (
			<div className="boks">
				<PdlPersonInfo data={hentPerson} />
				<IdentInfo pdlResponse={hentIdenter} />
				<GeografiskTilknytning data={hentGeografiskTilknytning} />
				<PdlNasjonalitet data={hentPerson} />
				<Telefonnummer data={telefonnummer} />
				<Boadresse data={bostedsadresse} />
				<Oppholdsadresse data={oppholdsadresse} />
				<Kontaktadresse data={kontaktadresse} />
				<Adressebeskyttelse data={adressebeskyttelse} />
				<PdlFullmakt data={fullmakt} />
				<PdlSikkerhetstiltak data={sikkerhetstiltak} />
			</div>
		)
	}

	return (
		<div className="flexbox--flex-wrap">
			<Tooltip
				overlay={getPersonInfo()}
				placement="top"
				align={{
					offset: ['0', '-10'],
				}}
				mouseEnterDelay={0.1}
				mouseLeaveDelay={0.1}
				arrowContent={<div className="rc-tooltip-arrow-inner"></div>}
				overlayStyle={{ opacity: 1 }}
			>
				<div className="miljoe-knapp">PDL</div>
			</Tooltip>
		</div>
	)
}
