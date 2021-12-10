import React from 'react'
import './DataVisning.less'
// @ts-ignore
import Tooltip from 'rc-tooltip'
import 'rc-tooltip/assets/bootstrap_white.css'
import { IdentInfo } from '~/components/fagsystem/pdlf/visning/partials/Identinfo'
import { GeografiskTilknytning } from '~/components/fagsystem/pdlf/visning/partials/GeografiskTilknytning'
import { PdlPersonInfo } from '~/components/fagsystem/pdlf/visning/partials/PdlPersonInfo'
import { PdlNasjonalitet } from '~/components/fagsystem/pdlf/visning/partials/PdlNasjonalitet'
import { PdlBoadresse } from '~/components/fagsystem/pdlf/visning/partials/PdlBoadresse'
import { PdlFullmakt } from '~/components/fagsystem/pdlf/visning/partials/PdlFullmakt'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'

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
	fullmakt: [FullmaktData]
	telefonnummer: Array<TelefonData>
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

export const PdlDataVisning = ({ data }: PdlData) => {
	if (!data || !data.hentPerson) {
		return null
	}

	const gjeldendeAdresse = (adresseListe: Array<BostedData>) => {
		if (!adresseListe || adresseListe.length === 0) return null
		const filteredArray = adresseListe.filter((adresse: BostedData) => !adresse.metadata.historisk)
		return filteredArray.length > 0 ? filteredArray : adresseListe
	}

	const getPersonInfo = (identInfo: Data) => {
		return (
			<div className="boks">
				<PdlPersonInfo data={identInfo.hentPerson} />
				<IdentInfo pdlResponse={identInfo.hentIdenter} />
				<GeografiskTilknytning data={identInfo.hentGeografiskTilknytning} />
				<PdlNasjonalitet data={identInfo.hentPerson} />
				<PdlBoadresse data={gjeldendeAdresse(identInfo.hentPerson.bostedsadresse)} />
				<Telefonnummer data={identInfo.hentPerson.telefonnummer} />
				<PdlFullmakt data={identInfo.hentPerson.fullmakt} />
			</div>
		)
	}

	return (
		<div className="flexbox--flex-wrap">
			<Tooltip
				overlay={getPersonInfo(data)}
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
