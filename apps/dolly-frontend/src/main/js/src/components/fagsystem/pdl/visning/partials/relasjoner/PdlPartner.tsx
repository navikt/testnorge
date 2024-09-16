import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { getSortedSivilstand } from '@/components/fagsystem/pdl/visning/partials/utils'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import { SivilstandData } from '@/components/fagsystem/pdlf/PdlTypes'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import React from 'react'
import { SivilstandVisning } from '@/components/fagsystem/pdlf/visning/partials/Sivilstand'

type VisningProps = {
	data: SivilstandData
	idx?: number
	alleData?: Array<SivilstandData>
	tmpPersoner?: any
	ident?: string
	identtype?: string
	master?: string
}

type PdlPartnerProps = {
	data: SivilstandData[]
	pdlfData?: SivilstandData[]
	tmpPersoner?: any
	ident?: string
	identtype?: string
}

const PartnerVisning = ({ data, idx }: VisningProps) => {
	console.log('data: ', data) //TODO - SLETT MEG
	const harPartner = data.type !== 'UGIFT'
	return (
		<div key={idx} className="person-visning_content">
			<TitleValue
				title={harPartner ? 'Forhold til partner (sivilstand)' : 'Sivilstand'}
				kodeverk={PersoninformasjonKodeverk.Sivilstander}
				value={data.type}
				size="medium"
			/>
			{harPartner && (
				<TitleValue title="Partnerident" value={data.relatertVedSivilstand} visKopier />
			)}
			<TitleValue title="Sivilstand fra dato" value={formatDate(data.gyldigFraOgMed)} />
			<TitleValue title="Bekreftelsesdato" value={formatDate(data.bekreftelsesdato)} />
			<TitleValue title="Master" value={data.metadata?.master} />
		</div>
	)
}

const PartnerVisningRedigerbar = ({
	data,
	idx,
	alleData,
	tmpPersoner,
	ident,
	identtype,
}: VisningProps) => {
	return (
		<div className="person-visning_content">
			<SivilstandVisning
				sivilstandData={data}
				idx={idx}
				data={alleData}
				relasjoner={null} //TODO - Send med relasjoner
				tmpPersoner={tmpPersoner}
				ident={ident}
				identtype={identtype}
			/>
		</div>
	)
}

export const PdlPartner = ({ data, pdlfData, tmpPersoner, ident, identtype }: PdlPartnerProps) => {
	const partnere = getSortedSivilstand(data)
	if (
		(!partnere || partnere.length) === 0 &&
		(!tmpPersoner || Object.keys(tmpPersoner).length < 1)
	) {
		return null
	}

	const gjeldendePartnere = partnere.filter(
		(partner: SivilstandData) => !partner.metadata?.historisk,
	)
	const historiskePartnere = partnere.filter(
		(partner: SivilstandData) => partner.metadata?.historisk,
	)

	return (
		<div>
			<SubOverskrift label="Sivilstand (partner)" iconKind="partner" />
			<ArrayHistorikk
				component={PartnerVisning}
				componentRedigerbar={PartnerVisningRedigerbar}
				data={gjeldendePartnere}
				pdlfData={pdlfData}
				historiskData={historiskePartnere}
				tmpPersoner={tmpPersoner}
				ident={ident}
				identtype={identtype}
				header={''}
			/>
		</div>
	)
}
